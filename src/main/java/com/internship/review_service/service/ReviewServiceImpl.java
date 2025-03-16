package com.internship.review_service.service;


import com.internship.review_service.dto.ReviewCreateDto;
import com.internship.review_service.dto.ReviewDto;
import com.internship.review_service.dto.UserDto;
import com.internship.review_service.exception.NoReviewsOnResource;
import com.internship.review_service.exception.NotFoundException;
import com.internship.review_service.exception.UnknownUserIdException;
import com.internship.review_service.feign.JobService;
import com.internship.review_service.feign.UserService;
import com.internship.review_service.mapper.ReviewMapper;
import com.internship.review_service.model.JobReview;
import com.internship.review_service.model.UserReview;
import com.internship.review_service.rabbitmq.producer.ReviewMessageProducer;
import com.internship.review_service.repository.JobReviewRepository;
import com.internship.review_service.repository.StatusRepository;
import com.internship.review_service.repository.UserReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{

    private final ReviewMessageProducer addedReviewProducer;

    private final ReviewMapper reviewMapper;

    private final UserReviewRepository userReviewRepository;

    private final JobService jobService;

    private final JobReviewRepository jobReviewRepository;

    private final StatusRepository statusRepository;

    private final UserService userService;

    /**
     * Creates a new user review.
     * @param userId the id of the user creating the review
     * @param reviewCreateDto the review data
     * @return the created review
     */
    @Transactional
    @Override
    public ReviewDto addUserReview(Long userId, ReviewCreateDto reviewCreateDto) {

        UserDto user = userService.getUser(userId).getBody();

        UserReview review = reviewMapper.toEntity(reviewCreateDto);
        review.setStatusId(statusRepository.findStatusByStatusId(3L));
        review.setReviewerId(userId);

        addedReviewProducer.sendAddedReviewMessage(user.email());

        return reviewMapper.toDto(userReviewRepository.save(review));
    }

    /**
     * Creates a new job review.
     * @param userId the id of the user creating the review
     * @param jobId the id of the job being reviewed
     * @param reviewCreateDto the review data
     * @return the created review
     */
    @Transactional
    @Override
    public ReviewDto addJobReview(Long userId,Long jobId, ReviewCreateDto reviewCreateDto) {
        userService.getUser(userId);

        jobService.getJobById(jobId);

        JobReview review = reviewMapper.toJobEntity(reviewCreateDto);

        review.setReviewerId(userId);

        review.setJobId(jobId);

        return reviewMapper.toJobDto(jobReviewRepository.save(review));
    }

    /**
     * Deletes a user review by its id, but only if the user requesting the deletion is the same as the user that created the review.
     * @param userId the id of the user requesting the deletion
     * @param reviewId the id of the review to be deleted
     * @throws NotFoundException if no review with the given id exists
     * @throws UnknownUserIdException if the user requesting the deletion is not the same as the user that created the review
     */
    @Transactional
    @Override
    public void deleteUserReview(Long userId, Long reviewId) { //Integrate the logic with token for the userId?

        Optional<UserReview> review = userReviewRepository.findById(reviewId);

        if(review.isEmpty())
            throw new NotFoundException("Review with this id not found! id: " + reviewId);

        if(!review.get().getReviewerId().equals(userId))
            throw new UnknownUserIdException("This user does not own this review! id: " +userId);

        userReviewRepository.delete(review.get());
    }

    /**
     * Finds a user review by its id.
     * @param reviewId the id of the review
     * @return the found review
     * @throws NotFoundException if no review with the given id exists
     */
    @Override
    public ReviewDto getUserReview(Long reviewId) {

        Optional<UserReview> review = userReviewRepository.findById(reviewId);

        if(review.isEmpty())
            throw new NotFoundException("Review with this id not found! id: " + reviewId);


        return reviewMapper.toDto(review.get());
    }

    /**
     * Finds all reviews for a given job.
     * @param jobId the id of the job
     * @return a list of all reviews for the given job
     * @throws NoReviewsOnResource if no reviews are found for the given job
     */
    @Override
    public List<ReviewDto> getAllJobReviews(Long jobId) {

        jobService.getJobById(jobId);

        List<JobReview> jobReviews = jobReviewRepository.findAllByJobId(jobId);

        if(jobReviews.isEmpty())
            throw new NoReviewsOnResource("No reviews found for this job! id: " + jobId);

        Stream<ReviewDto> streamOfJobReviews = jobReviews
                .stream()
                .map(reviewMapper::toJobDto);

        return streamOfJobReviews.toList();
    }

}
