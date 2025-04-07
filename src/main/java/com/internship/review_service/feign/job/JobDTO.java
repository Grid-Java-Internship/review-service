package com.internship.review_service.feign.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class JobDTO {
    private Long id;

    private Long userId;

    private String title;

    private String description;

    private java.time.LocalDate dateOfPosting;

    private Integer experience;

    private Integer hourlyRate;

    private List<String> savedMedia;
}
