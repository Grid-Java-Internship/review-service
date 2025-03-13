package com.internship.review_service.exception;

public class NoReviewsOnResource extends RuntimeException {
    public NoReviewsOnResource(String message) {
        super(message);
    }
}
