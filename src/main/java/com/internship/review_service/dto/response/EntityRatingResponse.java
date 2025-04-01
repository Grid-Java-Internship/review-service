package com.internship.review_service.dto.response;

import com.internship.review_service.enums.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityRatingResponse {

    private Long id;
    private ReviewType reviewType;
    private Double rating;
    private Integer reviewCount;
}
