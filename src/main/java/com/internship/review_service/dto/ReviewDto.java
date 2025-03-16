package com.internship.review_service.dto;


public record ReviewDto(
        Long reviewerId,

        Long jobId,

        Long reviewId,

        String text,

        String title
) {

}
