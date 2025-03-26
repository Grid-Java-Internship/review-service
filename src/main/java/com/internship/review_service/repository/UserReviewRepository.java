package com.internship.review_service.repository;

import com.internship.review_service.model.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Long> {

    UserReview findByUserReviewId(Long reviewId);


}
