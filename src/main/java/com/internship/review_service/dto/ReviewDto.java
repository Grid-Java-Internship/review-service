package com.internship.review_service.dto;


public record ReviewDto(
        Long review_id,

        String text,

        String title
) {

}
