package com.internship.review_service.repository;

import com.internship.review_service.model.JobReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobReviewRepository extends JpaRepository<JobReview, Long> {

    List<JobReview> findAllByJobId(Long jobId);

}
