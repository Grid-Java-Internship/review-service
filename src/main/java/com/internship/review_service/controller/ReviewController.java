package com.internship.review_service.controller;

import com.internship.review_service.dto.ReviewCreateDto;
import com.internship.review_service.dto.ReviewDto;
import com.internship.review_service.dto.ReviewEditDto;
import com.internship.review_service.exception.NotFoundException;
import com.internship.review_service.exception.UnknownUserIdException;
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

    /**
     * Retrieves a user review by its id.
     * @param reviewId the id of the review
     * @return the found review
     */
    @GetMapping("user/getr/{revid}")
    public ResponseEntity<ReviewDto> getUserReview(@PathVariable("revid") Long reviewId) {

        return ResponseEntity.ok()
                .body(reviewService.getUserReview(reviewId));
    }

    /**
     * Retrieves all reviews for a given job.
     * @param jobid the id of the job
     * @return a list of all reviews for the given job
     */
    @GetMapping("job/getr/{jobid}")
    public List<ReviewDto> getJobReviews(@PathVariable("jobid") Long jobid) {
        return reviewService.getAllJobReviews(jobid);
    }

    /**
     * Creates a new user review.
     * @param userId the id of the user creating the review
     * @param reviewCreateDto the review data
     * @return the created review wrapped in a ResponseEntity
     */
    @PostMapping("user/addreview/{id}")
    public ResponseEntity<ReviewDto> createUserReview(@PathVariable("id") Long userId,
                                                 @RequestBody ReviewCreateDto reviewCreateDto) {

        return ResponseEntity
                .ok()
                .body(reviewService.addUserReview(userId,reviewCreateDto));
    }

    /**
     * Creates a new job review.
     * @param userId the id of the user creating the review
     * @param jobId the id of the job being reviewed
     * @param reviewCreateDto the review data
     * @return the created review wrapped in a ResponseEntity
     */
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

    /**
     * Deletes a user review by its id, but only if the user requesting the deletion is the same as the user that created the review.
     * @param reviewId the id of the review to be deleted
     * @param userId the id of the user requesting the deletion
     * @return an empty ResponseEntity
     * @throws NotFoundException if no review with the given id exists
     * @throws UnknownUserIdException if the user requesting the deletion is not the same as the user that created the review
     */
    @DeleteMapping("user/{revid}/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("revid") Long reviewId,@PathVariable("id") Long userId) {

        reviewService.deleteUserReview(userId,reviewId);

        return ResponseEntity.ok().build();
    }

}
