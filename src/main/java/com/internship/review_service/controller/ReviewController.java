package com.internship.review_service.controller;

import com.internship.review_service.dto.ReviewCreateDto;
import com.internship.review_service.dto.ReviewDto;
import com.internship.review_service.dto.ReviewEditDto;
import com.internship.review_service.service.ReviewService;
import com.internship.review_service.service.ReviewServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review/")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ReviewDto> getReview() {
        return ResponseEntity.ok(new ReviewDto(1L,"a","a"));
    }

    @PostMapping("user/addreview/{id}")
    public ResponseEntity<ReviewDto> createUserReview(@PathVariable("id") Long userId,
                                                 @RequestBody ReviewCreateDto reviewCreateDto) {

        return ResponseEntity
                .ok()
                .body(reviewService.addUserReview(userId,reviewCreateDto));
    }

    @PostMapping("job/addreview")
    public ResponseEntity<Void> createJobReview() {

        return ResponseEntity.ok().build();
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Void> editReview(@PathVariable("id") Long reviewId,
                                           @RequestBody ReviewEditDto reviewDto) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Page<ReviewDto>> getAllReviewsForAUser(@PathVariable("id") Long userId){

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("user/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") Long reviewId) {

        return ResponseEntity.ok().build();
    }

}
