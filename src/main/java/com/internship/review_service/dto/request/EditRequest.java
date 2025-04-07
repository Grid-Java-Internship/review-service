package com.internship.review_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditRequest {

    private Long id;
    private Long userId;
    private String text;
    private Integer rating;
}
