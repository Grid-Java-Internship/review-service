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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewMessageConsumer {

    private final ReviewRepository reviewRepository;

    @Transactional
    @RabbitListener(queues = "${spring.rabbitmq.queues.disableReviews}")
    public void disableJob(Message message) {
        log.info("Attempting to disable reviews for job with id: {}", message.getId());

        List<Review> reviews= reviewRepository.findByReviewedIdAndReviewTypeAndStatusNot(message.getId(), ReviewType.JOB, Status.DISABLED);
        if(reviews.isEmpty()){
            throw new NotFoundException("Reviews for job with id: " + message.getId() + " not found or already disabled");
        }
        log.info("Reviews are: {}",reviews);
        for(Review review : reviews) {
            review.setStatus(Status.DISABLED);
        }

        log.info("Reviews for job with id: {} disabled successfully", message.getId());

    }

    @Transactional
    @RabbitListener(queues = "${spring.rabbitmq.queues.enableReviews}")
    public void enableJob(Message message) {
        log.info("Attempting to enable reviews for job with id: {}", message.getId());

        List<Review> reviews = reviewRepository.findByReviewedIdAndReviewTypeAndStatus(message.getId(), ReviewType.JOB, Status.DISABLED);
        if(reviews.isEmpty()){
            throw new NotFoundException("Reviews with id: " + message.getId() + " not found or already enabled");
        }
        for (Review review : reviews) {
            review.setStatus(Status.ACCEPTED);
        }

        log.info("Reviews for job with id: {} enabled successfully", message.getId());
    }
}

