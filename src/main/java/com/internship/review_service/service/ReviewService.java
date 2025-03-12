package com.internship.review_service.service;

import com.internship.review_service.dto.ReviewCreateDto;
import com.internship.review_service.dto.ReviewDto;

public interface ReviewService {

    ReviewDto addUserReview(Long userId, ReviewCreateDto reviewCreateDto);

    ReviewDto addJobReview(Long userId, Long jobId, ReviewCreateDto reviewCreateDto);

    void deleteUserReview(Long userId, Long reviewId);

    ReviewDto getUserReview(Long reviewId);

}
