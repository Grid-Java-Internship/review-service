package com.internship.review_service.dto;


import com.internship.review_service.model.Status;

import java.time.LocalDate;

public record ReviewEditDto(
        Long userReviewId,

        Long reviewerId,

        Long reviewId,

        Integer rating,

        String text,

        LocalDate reviewDate,

        Status statusId
) {

}
