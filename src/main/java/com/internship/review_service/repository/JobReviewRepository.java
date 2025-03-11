package com.internship.review_service.repository;

import com.internship.review_service.model.JobReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobReviewRepository extends JpaRepository<JobReview, Long> {



}
