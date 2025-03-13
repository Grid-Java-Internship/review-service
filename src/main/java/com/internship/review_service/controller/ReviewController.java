package com.internship.review_service.controller;

import com.internship.review_service.dto.ReviewCreateDto;
import com.internship.review_service.dto.ReviewDto;
import com.internship.review_service.dto.ReviewEditDto;
import com.internship.review_service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/review/")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("user/getr/{revid}")
    public ResponseEntity<ReviewDto> getUserReview(@PathVariable("revid") Long reviewId) {

        return ResponseEntity.ok()
                .body(reviewService.getUserReview(reviewId));
    }

    @GetMapping("job/getr/{jobid}")
    public List<ReviewDto> getJobReviews(@PathVariable("jobid") Long jobid) {
        return reviewService.getAllJobReviews(jobid);
    }

    @PostMapping("user/addreview/{id}")
    public ResponseEntity<ReviewDto> createUserReview(@PathVariable("id") Long userId,
                                                 @RequestBody ReviewCreateDto reviewCreateDto) {

        return ResponseEntity
                .ok()
                .body(reviewService.addUserReview(userId,reviewCreateDto));
    }

    @PostMapping("job/addreview/{id}/{jobid}")
    public ResponseEntity<ReviewDto> createJobReview(@PathVariable("id") Long userId,
                                                @PathVariable("jobid") Long jobId,
                                                @RequestBody ReviewCreateDto reviewCreateDto) {

        return ResponseEntity.ok().body(reviewService.addJobReview(userId, jobId, reviewCreateDto));
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

    @DeleteMapping("user/{revid}/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("revid") Long reviewId,@PathVariable("id") Long userId) {

        reviewService.deleteUserReview(userId,reviewId);

        return ResponseEntity.ok().build();
    }

}
