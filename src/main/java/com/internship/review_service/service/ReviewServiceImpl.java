package com.internship.review_service.service;


import com.internship.review_service.feign.job.JobDTO;
import com.internship.review_service.feign.user.UserDTO;
import com.internship.review_service.dto.request.ReviewRequest;
import com.internship.review_service.dto.response.ReviewResponse;
import com.internship.review_service.enums.ReviewType;
import com.internship.review_service.enums.Status;
import com.internship.review_service.exception.ConflictException;
import com.internship.review_service.exception.NotFoundException;
import com.internship.review_service.feign.job.JobService;
import com.internship.review_service.feign.user.UserService;
import com.internship.review_service.mapper.ReviewMapper;
import com.internship.review_service.model.Review;
import com.internship.review_service.rabbitmq.producer.ReviewMessageProducer;
import com.internship.review_service.repository.ReviewRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMessageProducer reviewMessageProducer;

    private final ReviewMapper reviewMapper;

    private final ReviewRepository reviewRepository;

    private final JobService jobService;

    private final UserService userService;

    /**
     * Adds a review based on the given ReviewRequest. Validates the request to ensure
     * that a user is not reviewing themselves and that the reviewed entity exists.
     * Transforms the request into a Review entity, sets its status to ACCEPTED,
     * saves it to the repository, and sends a notification message.
     *
     * @param request the ReviewRequest containing details of the review
     * @return true if the review is successfully added
     * @throws ConflictException if the user attempts to review themselves
     * @throws NotFoundException if the user or reviewed entity is not found
     */
    @Override
    public Boolean addReview(ReviewRequest request) {

        reviewRepository.findByUserIdAndReviewedIdAndReviewType(
                request.getUserId(),
                request.getReviewedId(),
                request.getReviewType()
        ).ifPresent(exists -> {
            throw new ConflictException("You have already reviewed this " + request.getReviewType());
        });

        if (ReviewType.USER.equals(request.getReviewType()) && Objects.equals(request.getUserId(), request.getReviewedId())) {
            throw new ConflictException("User cannot review themselves.");
        }

        try {
            UserDTO user = userService.getUser(request.getUserId());

            exists(request.getUserId(), request.getReviewedId(), request.getReviewType());
            Review review = reviewMapper.toEntity(request);
            review.setStatus(Status.ACCEPTED);

            reviewRepository.save(review);
            reviewMessageProducer.sendAddedReviewMessage(user.getEmail(), request.getUserId());

            log.info("User {} reviewed {} with type {}", request.getUserId(), request.getReviewedId(), request.getReviewType());
            return true;
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("User with this id not found! id: " + request.getUserId());
        }
    }

    /**
     * Deletes a review if it exists and the user has permission to delete it.
     *
     * @param userId the ID of the user attempting to delete the review
     * @param reviewId the ID of the review to be deleted
     * @return true if the review was successfully deleted
     * @throws NotFoundException if the review with the given ID is not found
     * @throws ConflictException if the user does not have permission to delete the review
     */
    @Override
    @Transactional
    public Boolean deleteReview(Long userId, Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review with this id not found! id: " + reviewId));

        if (!Objects.equals(review.getUserId(), userId)) {
            throw new ConflictException("User cannot delete another user's review!");
        }

        reviewRepository.delete(review);

        return true;
    }

    /**
     * Retrieves a review by its unique identifier.
     *
     * @param reviewId the id of the review to retrieve
     * @return a ReviewResponse object representing the review
     * @throws NotFoundException if no review is found with the given id
     */
    @Override
    public ReviewResponse getReview(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review with this id not found! id: " + reviewId));

        return reviewMapper.toDto(review);
    }

    /**
     * Gets all reviews for a given reviewed entity (user or job).
     *
     * @param reviewedId the id of the entity being reviewed
     * @param page the page number to retrieve
     * @return a list of ReviewResponse objects, each representing a review
     */
    @Override
    public List<ReviewResponse> getAllReviews(Long reviewedId, int page) {
        Pageable pageable = PageRequest.of(page, 10);

        List<Review> reviews = reviewRepository.findByReviewedId(reviewedId, pageable).getContent();

        return reviews.stream().map(reviewMapper::toDto).toList();
    }

    /**
     * Checks if a user with the given id exists in the user service,
     * and if the given job id exists in the job service.
     * If the user id and the job id are the same, a
     * ConflictException is thrown.
     *
     * @param userId the id of the user
     * @param reviewedId the id of the job or user being reviewed
     * @param type the type of review
     * @throws NotFoundException if the user or job does not exist
     * @throws ConflictException if the user is trying to review their own job
     */
    private void exists(Long userId, Long reviewedId, ReviewType type) {

        JobDTO job;

        switch (type) {
            case USER:
                try {
                    userService.getUser(reviewedId);
                    break;
                } catch (FeignException.NotFound e) {
                    throw new NotFoundException("User with this id not found! id: " + userId);
                }
            case JOB:
                try {
                    job = jobService.getJobById(reviewedId);
                } catch (FeignException.NotFound e) {
                    throw new NotFoundException("Job with this id not found! id: " + userId);
                }

                if (Objects.equals(job.getUserId(), userId)) {
                    throw new ConflictException("User cannot review their own job! id: " + userId);
                }
                break;
        }
    }
}
