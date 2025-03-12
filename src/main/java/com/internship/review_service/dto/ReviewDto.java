package com.internship.review_service.dto;


public record ReviewDto(
        Long reviewerId,

        Long jobId,

        Long review_id,

        String text,

        String title
) {

}
