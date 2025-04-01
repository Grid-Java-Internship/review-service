package com.internship.review_service.dto.response;

import com.internship.review_service.enums.ReviewType;
import com.internship.review_service.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private Long userId;
    private Long reviewedId;
    private ReviewType reviewType;
    private Status status;
    private Integer rating;
    private String text;
    private String reviewDate;
}
