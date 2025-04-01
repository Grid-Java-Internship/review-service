package com.internship.review_service.service;

import com.internship.review_service.dto.request.ReviewRequest;
import com.internship.review_service.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    Boolean addReview(ReviewRequest reviewRequest);

    Boolean deleteReview(Long userId, Long reviewId);

    ReviewResponse getReview(Long reviewId);

    List<ReviewResponse> getAllReviews(Long reviewedId, int page);

}
