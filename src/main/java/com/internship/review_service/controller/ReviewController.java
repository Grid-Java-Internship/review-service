package com.internship.review_service.controller;

import com.internship.review_service.dto.request.EditRequest;
import com.internship.review_service.dto.request.ReviewRequest;
import com.internship.review_service.dto.response.EntityRatingResponse;
import com.internship.review_service.dto.response.ReviewResponse;
import com.internship.review_service.dto.response.SimpleMessageResponse;
import com.internship.review_service.enums.ReviewType;
import com.internship.review_service.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private static final String REVIEW_SUCCESS = "Reviewed successfully";
    private static final String REVIEW_FAILED = "Review failed";
    private static final String DELETE_SUCCESS = "Review deleted successfully";
    private static final String DELETE_FAILED = "Review deletion failed";

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<SimpleMessageResponse> addReview(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal String userId) {
        boolean result = reviewService.addReview(request, Long.parseLong(userId));

        String message = result ? REVIEW_SUCCESS : REVIEW_FAILED;
        HttpStatus status = result ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;

        SimpleMessageResponse response = SimpleMessageResponse.builder()
                .statusCode(status.value())
                .message(message)
                .success(result)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SimpleMessageResponse> deleteReview(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal String userId
    ) {
        boolean result = reviewService.deleteReview(Long.parseLong(userId), id);

        String message = result ? DELETE_SUCCESS : DELETE_FAILED;
        HttpStatus status = result ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;

        SimpleMessageResponse response = SimpleMessageResponse.builder()
                .statusCode(status.value())
                .message(message)
                .success(result)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReview(id));
    }

    @GetMapping("/{type}/{id}")
    public ResponseEntity<List<ReviewResponse>> getEntityReviews(
            @PathVariable("type") ReviewType type,
            @PathVariable("id") Long reviewedId,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.getAllReviews(
                        type,
                        reviewedId,
                        page
                ));
    }

    @PutMapping("/edit")
    public ResponseEntity<ReviewResponse> editReview(
            @Valid @RequestBody EditRequest request,
            @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reviewService.editReview(request, Long.parseLong(userId)));
    }

    @GetMapping("/rating/{type}/{id}")
    public ResponseEntity<EntityRatingResponse> getRating(
            @PathVariable("type") ReviewType type,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getEntityRating(id, type));
    }
}
