package com.internship.review_service.service;


import com.internship.review_service.dto.request.EditRequest;
import com.internship.review_service.dto.response.EntityRatingResponse;
import com.internship.review_service.feign.job.JobDTO;
import com.internship.review_service.feign.reservation.GetReservationResponse;
import com.internship.review_service.feign.reservation.ReservationService;
import com.internship.review_service.feign.reservation.ReservationStatus;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.internship.review_service.enums.ReviewType.JOB;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMessageProducer reviewMessageProducer;

    private final ReviewMapper reviewMapper;

    private final ReviewRepository reviewRepository;

    private final JobService jobService;

    private final UserService userService;

    private final ReservationService reservationService;

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
    public Boolean addReview(ReviewRequest request, Long userId) {

        reviewRepository.findByUserIdAndReviewedIdAndReviewType(
                userId,
                request.getReviewedId(),
                request.getReviewType()
        ).ifPresent(exists -> {
            if (exists.getStatus().equals(Status.RECEIVED) ||
                exists.getStatus().equals(Status.IN_REVIEW) ||
                exists.getStatus().equals(Status.ACCEPTED)
            ) {
                throw new ConflictException("You have already reviewed this " + request.getReviewType());
            }
        });

        if (ReviewType.USER.equals(request.getReviewType()) && Objects.equals(userId, request.getReviewedId())) {
            throw new ConflictException("User cannot review themselves.");
        }

        try {
            UserDTO user = userService.getUser(userId);

            exists(userId, request.getReviewedId(), request.getReviewType());
            Review review = reviewMapper.toEntity(request);
            review.setUserId(userId);
            review.setStatus(Status.ACCEPTED);

            reviewRepository.save(review);
            reviewMessageProducer.sendAddedReviewMessage(user.getEmail(), userId);

            log.info("User {} reviewed {} with type {}", userId, request.getReviewedId(), request.getReviewType());
            return true;
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("User with this id not found! id: " + userId);
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

        review.setStatus(Status.DELETED);

        reviewRepository.save(review);

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
    public List<ReviewResponse> getAllReviews(ReviewType type, Long reviewedId, int page) {
        Pageable pageable = PageRequest.of(page, 10);

        List<Review> reviews = reviewRepository.findByReviewedIdAndStatusAndReviewType(
                reviewedId,
                Status.ACCEPTED,
                type,
                pageable
        ).getContent();

        return reviews.stream().map(reviewMapper::toDto).toList();
    }

    /**
     * Edits a review, checking that the user attempting to edit it is the same one who wrote it.
     *
     * @param editRequest a {@link EditRequest} object containing the new text and rating for the review
     * @return the edited review as a {@link ReviewResponse} object
     * @throws NotFoundException if no review is found with the given id
     * @throws ConflictException if the user attempting to edit the review is not the same one who wrote it
     */
    @Override
    public ReviewResponse editReview(EditRequest editRequest, Long userId) {

        Review review = reviewRepository.findById(editRequest.getId())
                .orElseThrow(() -> new NotFoundException("Review with this id not found! id: " + editRequest.getId()));

        if (!Objects.equals(review.getUserId(), userId)) {
            throw new ConflictException("User cannot edit another user's review!");
        }

        review.setText(editRequest.getText());
        review.setRating(editRequest.getRating());
        review.setReviewDate(LocalDate.now());

        reviewRepository.save(review);

        return reviewMapper.toDto(review);
    }

    /**
     * Gets the rating of an entity (user or job).
     *
     * @param reviewedId the id of the entity being reviewed
     * @param reviewType the type of review
     * @return an EntityRatingResponse object containing the id, review count and average rating
     * @throws NotFoundException if the entity does not exist
     */
    @Override
    public EntityRatingResponse getEntityRating(Long reviewedId, ReviewType reviewType) {

        List<Review> reviews = reviewRepository.findByReviewedIdAndReviewTypeAndStatus(
                reviewedId,
                reviewType,
                Status.ACCEPTED
        );

        EntityRatingResponse response = EntityRatingResponse.builder()
                .id(reviewedId)
                .reviewCount(reviews.size())
                .reviewType(reviewType)
                .build();

        double rating = reviews.isEmpty()
                ? 0
                : (double) reviews.stream().mapToInt(Review::getRating).sum() / reviews.size();

        response.setRating(
                rating
        );

        return response;
    }

    @Override
    public List<ReviewResponse> getUserLeftReviews(Long id, int page,Status status) {
        Pageable pageable = PageRequest.of(page, 10);

        List<Review> reviews = reviewRepository.findByUserIdAndStatus(
                id,
                status,
                pageable
        ).getContent();

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
                    throw new NotFoundException("User with this id not found! id: " + reviewedId);
                }
            case JOB:
                try {
                    job = jobService.getJobById(reviewedId);
                } catch (FeignException.NotFound e) {
                    throw new NotFoundException("Job with this id not found! id: " + reviewedId);
                }

                if (Objects.equals(job.getUserId(), userId)) {
                    throw new ConflictException("User cannot review their own job! id: " + userId);
                }
                break;
        }

        try {
            List<GetReservationResponse> reservations = reservationService
                    .getReservationsByCustomerId(userId, 0, 10)
                    .getBody();

            if (reservations == null || reservations.isEmpty()) {
                throw new NotFoundException("You must have a job with this user first! id: " + userId);
            }

            reservations = switch (type) {
                case USER -> reservations.stream()
                        .filter(r -> r.getWorkerId().equals(reviewedId))
                        .toList();
                case JOB -> reservations.stream()
                        .filter(r -> r.getJobId().equals(reviewedId))
                        .toList();
            };

            boolean hasFinished = reservations.stream()
                    .anyMatch(r -> ReservationStatus.FINISHED == r.getStatus());

            if (reservations.isEmpty() || !hasFinished) {
                throw new NotFoundException("You must have a finished job with the reviewed first!");
            }

        } catch (FeignException.NotFound e) {
            log.error("You must have a job with this user first! id: {}", userId);
            throw new NotFoundException("You must have a job with this user first! id: " + userId);
        }
    }
}
