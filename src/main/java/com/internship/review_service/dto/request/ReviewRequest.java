package com.internship.review_service.dto.request;

import com.internship.review_service.enums.ReviewType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotNull(message = "Reviewed ID cannot be null.")
    private Long reviewedId;

    @NotNull(message = "Review type cannot be null.")
    private ReviewType reviewType;

    @NotNull(message = "Rating cannot be null.")
    private Integer rating;

    @NotNull(message = "Text cannot be null.")
    private String text;
}
