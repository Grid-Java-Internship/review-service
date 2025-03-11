package com.internship.review_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "status")
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long statusId;

    private String statusType;

    @OneToMany
    private List<UserReview> userReviews;

    @OneToMany
    private List<JobReview> jobReviews;
}
