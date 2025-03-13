package com.internship.review_service.service;


import com.internship.review_service.dto.ReviewCreateDto;
import com.internship.review_service.dto.ReviewDto;
import com.internship.review_service.exception.NoReviewsOnResource;
import com.internship.review_service.exception.NotFoundException;
import com.internship.review_service.exception.UnknownUserIdException;
import com.internship.review_service.feign.JobService;
import com.internship.review_service.feign.UserService;
import com.internship.review_service.mapper.ReviewMapper;
import com.internship.review_service.model.JobReview;
import com.internship.review_service.model.Review;
import com.internship.review_service.model.UserReview;
import com.internship.review_service.rabbitmq.producer.AddedReviewProducer;
import com.internship.review_service.repository.JobReviewRepository;
import com.internship.review_service.repository.StatusRepository;
import com.internship.review_service.repository.UserReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{

    private final AddedReviewProducer addedReviewProducer;

    private final ReviewMapper reviewMapper;

    private final UserReviewRepository userReviewRepository;

    private final JobService jobService;

    private final JobReviewRepository jobReviewRepository;

    private final StatusRepository statusRepository;

    private final UserService userService;

    @Transactional
    @Override
    public ReviewDto addUserReview(Long userId, ReviewCreateDto reviewCreateDto) {

        userService.getUser(userId);

        UserReview review = reviewMapper.toEntity(reviewCreateDto);

        review.setStatusId(statusRepository.findStatusByStatusId(3L));

        review.setReviewerId(userId);

        return reviewMapper.toDto(userReviewRepository.save(review));
    }

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

    @Override
    public ReviewDto getUserReview(Long reviewId) {

        Optional<UserReview> review = userReviewRepository.findById(reviewId);

        if(review.isEmpty())
            throw new NotFoundException("Review with this id not found! id: " + reviewId);


        return reviewMapper.toDto(review.get());
    }

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
