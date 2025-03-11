package com.internship.review_service.service;


import com.internship.review_service.dto.ReviewCreateDto;
import com.internship.review_service.dto.ReviewDto;
import com.internship.review_service.exception.NotFoundException;
import com.internship.review_service.feign.UserService;
import com.internship.review_service.mapper.ReviewMapper;
import com.internship.review_service.model.UserReview;
import com.internship.review_service.rabbitmq.producer.AddedReviewProducer;
import com.internship.review_service.repository.JobReviewRepository;
import com.internship.review_service.repository.StatusRepository;
import com.internship.review_service.repository.UserReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{

    private final AddedReviewProducer addedReviewProducer;

    private final ReviewMapper reviewMapper;

    private final UserReviewRepository userReviewRepository;

    private final JobReviewRepository jobReviewRepository;

    private final StatusRepository statusRepository;

    private final UserService userService;

    @Transactional
    @Override
    public ReviewDto addUserReview(Long userId, ReviewCreateDto reviewCreateDto) {

        userService.getUser(userId);

        UserReview review = reviewMapper.toEntity(reviewCreateDto);

        review.setStatusId(statusRepository.findStatusByStatusId(3L));

        return reviewMapper.toDto(userReviewRepository.save(review));
    }

    @Override
    public ReviewDto addJobReview(Long userId,Long jobId, ReviewCreateDto reviewCreateDto) {
        userService.getUser(userId);

        jobReviewRepository.findById(jobId).orElseThrow(() -> new NotFoundException("Job not found"));



        return null;
    }
}
