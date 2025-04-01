package com.internship.review_service.model;

import com.internship.review_service.enums.ReviewType;
import com.internship.review_service.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "User ID cannot be null.")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Reviewed ID cannot be null.")
    @Column(name = "reviewed_id", nullable = false)
    private Long reviewedId;

    @NotNull(message = "Review type cannot be null.")
    @Column(name = "review_type", nullable = false)
    private ReviewType reviewType;

    @NotNull(message = "Status cannot be null.")
    @Column(name = "status", nullable = false)
    private Status status;

    @NotNull(message = "Rating cannot be null.")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @NotNull(message = "Text cannot be null.")
    @Column(name = "text", nullable = false)
    private String text;

    @CreationTimestamp
    private LocalDate reviewDate;
}
