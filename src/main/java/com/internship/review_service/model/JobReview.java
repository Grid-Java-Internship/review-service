package com.internship.review_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
