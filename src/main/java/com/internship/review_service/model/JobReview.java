package com.internship.review_service.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "job_reviews")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class JobReview extends Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long jobReviewId;

    private Long jobId;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status statusId;
}
