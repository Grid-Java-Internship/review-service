package com.internship.review_service.rabbitmq.producer;

import com.internship.review_service.rabbitmq.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ReviewMessageProducer {
    private final AmqpTemplate amqpTemplate;

    @Value("${spring.rabbitmq.queues.producer.added-review}")
    private String addedReviewQueue;

    public void sendAddedReviewMessage(String emailTo, Long userId) {
        amqpTemplate.convertAndSend("reviews", addedReviewQueue, createAnAddedReviewMessage(emailTo, userId));
    }

    private Message createAnAddedReviewMessage(String emailTo, Long userId) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle("Added review confirmation");
        message.setContent("You have successfully added a review");
        message.setEmailTo(emailTo);

        return message;
    }
}
