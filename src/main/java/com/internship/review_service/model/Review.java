package com.internship.review_service.model;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@MappedSuperclass
public class Review {

    private Long reviewerId;

    private Integer rating;

    private String text;

    private LocalDate reviewDate;

    private String title;
}
