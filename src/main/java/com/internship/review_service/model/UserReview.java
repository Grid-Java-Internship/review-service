package com.internship.review_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_reviews")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserReview extends Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userReviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status")
    private Status statusId;
}
