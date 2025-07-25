package com.internship.review_service.service;

import com.internship.review_service.dto.request.EditRequest;
import com.internship.review_service.dto.request.ReviewRequest;
import com.internship.review_service.dto.response.EntityRatingResponse;
import com.internship.review_service.dto.response.ReviewResponse;
import com.internship.review_service.enums.ReviewType;
import com.internship.review_service.enums.Status;

import java.util.List;

public interface ReviewService {

    Boolean addReview(ReviewRequest reviewRequest, Long userId);

    Boolean deleteReview(Long userId, Long reviewId);

    ReviewResponse getReview(Long reviewId);

    List<ReviewResponse> getAllReviews(ReviewType type, Long reviewedId, int page);

    ReviewResponse editReview(EditRequest editRequest, Long userId);

    EntityRatingResponse getEntityRating(Long reviewedId, ReviewType reviewType);

    List<ReviewResponse> getUserLeftReviews(Long id, int page, Status status);
}
