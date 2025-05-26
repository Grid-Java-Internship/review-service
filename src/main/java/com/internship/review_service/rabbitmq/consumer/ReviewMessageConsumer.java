package com.internship.review_service.rabbitmq.consumer;

import com.internship.review_service.enums.ReviewType;
import com.internship.review_service.enums.Status;
import com.internship.review_service.exception.NotFoundException;
import com.internship.review_service.model.Review;
import com.internship.review_service.rabbitmq.Message;
import com.internship.review_service.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewMessageConsumer {

    private final ReviewRepository reviewRepository;

    @Transactional
    @RabbitListener(queues = "${spring.rabbitmq.queues.disableJob}")
    public void disableJob(Message message) {
        log.info("Attempting to disable reviews for job with id: {}", message.getId());

        List<Review> reviews = reviewRepository.findByReviewedIdAndReviewTypeAndStatusNot(message.getId(), ReviewType.JOB, Status.DISABLED);
        if (reviews.isEmpty()) {
            throw new NotFoundException("Reviews for job with id: " + message.getId() + " not found or already disabled");
        }
        log.info("Reviews are: {}", reviews);
        for (Review review : reviews) {
            review.setStatus(Status.DISABLED);
        }

        log.info("Reviews for job with id: {} disabled successfully", message.getId());
    }

    @Transactional
    @RabbitListener(queues = "${spring.rabbitmq.queues.enableJob}")
    public void enableJob(Message message) {
        log.info("Attempting to enable reviews for job with id: {}", message.getId());

        List<Review> reviews = reviewRepository.findByReviewedIdAndReviewTypeAndStatus(message.getId(), ReviewType.JOB, Status.DISABLED);
        if (reviews.isEmpty()) {
            throw new NotFoundException("Reviews with id: " + message.getId() + " not found or already enabled");
        }
        for (Review review : reviews) {
            review.setStatus(Status.ACCEPTED);
        }

        log.info("Reviews for job with id: {} enabled successfully", message.getId());
    }

    @Transactional
    @RabbitListener(queues = "${spring.rabbitmq.queues.deleteReviews}")
    public void deleteReviews(Message message) {
        log.info("Attempting to delete reviews for job with id: {}", message.getId());

        List<Review> reviews = reviewRepository.findReviewsByReviewedIdAndReviewType(message.getId(), ReviewType.JOB);
        if (reviews.isEmpty()) {
            throw new NotFoundException("No reviews associated with job: " + message.getId());
        }
        for (Review review : reviews) {
            review.setStatus(Status.DELETED);
            reviewRepository.save(review);
        }

        log.info("Reviews for job with id: {} deleted.", message.getId());
    }

    @Transactional
    @RabbitListener(queues = "${spring.rabbitmq.queues.disableUser}")
    public void disableUser(Message message) {
        log.info("Attempting to disable reviews with id {} for user.", message.getId());

        Optional<Review> review = reviewRepository.findById(message.getId());

        // Check if review is empty or already DISABLED
        if (review.isEmpty() || review.get().getStatus().equals(Status.DISABLED)) {
            log.error("Review with id: {} not found or already disabled", message.getId());
            throw new NotFoundException("Review with id: " + message.getId() + " not found or already disabled");
        }

        // DISABLE the review
        review.get().setStatus(Status.DISABLED);
        log.info("Review with id {} has been successfully disabled.", message.getId());
    }

    @Transactional
    @RabbitListener(queues = "${spring.rabbitmq.queues.enableUser}")
    public void enableUser(Message message) {
        log.info("Attempting to enable reviews with id {} for user.", message.getId());

        Optional<Review> review = reviewRepository.findById(message.getId());

        // Check if review is empty or already ENABLED
        if (review.isEmpty() || review.get().getStatus().equals(Status.ACCEPTED)) {
            throw new NotFoundException("Review with id: " + message.getId() + " not found or already enabled");
        }

        // ENABLE the review
        review.get().setStatus(Status.ACCEPTED);
        log.info("Review with id {} has been successfully enabled.", message.getId());
    }
}

