package com.internship.review_service.repository;

import com.internship.review_service.enums.ReviewType;
import com.internship.review_service.enums.Status;
import com.internship.review_service.model.Review;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByUserIdAndReviewedIdAndReviewType(
            @NotNull(message = "User ID cannot be null.") Long userId,
            @NotNull(message = "Reviewed ID cannot be null.") Long reviewedId,
            @NotNull(message = "Review type cannot be null.") ReviewType reviewType
    );

    List<Review> findByReviewedIdAndReviewTypeAndStatus(
            @NotNull(message = "Reviewed ID cannot be null.") Long reviewedId,
            @NotNull(message = "Review type cannot be null.") ReviewType reviewType,
            @NotNull(message = "Status cannot be null.") Status status
    );

    Page<Review> findByReviewedIdAndStatusAndReviewType(
            @NotNull(message = "Reviewed ID cannot be null.") Long reviewedId,
            @NotNull(message = "Status cannot be null.") Status status,
            @NotNull(message = "Review type cannot be null.") ReviewType reviewType,
            Pageable pageable);

    List<Review> findByReviewedIdAndReviewTypeAndStatusNot(
            @NotNull(message = "Reviewed ID cannot be null.") Long reviewedId,
            @NotNull(message = "Review type cannot be null.") ReviewType reviewType,
            @NotNull(message = "Status cannot be null.") Status status);

    Page<Review> findByUserIdAndStatus(Long id, Status status, Pageable pageable);
}
