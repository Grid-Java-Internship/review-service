package com.internship.review_service.controller;

import com.internship.review_service.dto.ReviewDto;
import com.internship.review_service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review/")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ReviewDto> getReview() {
        return ResponseEntity.ok(new ReviewDto(1L,"a","a"));
    }

    @PostMapping("/add")
    public ResponseEntity<Void> createReview() {
        reviewService.addNewReview();
        return ResponseEntity.ok().build();
    }
}
