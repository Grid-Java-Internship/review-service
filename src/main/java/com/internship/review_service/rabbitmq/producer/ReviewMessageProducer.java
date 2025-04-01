package com.internship.review_service.rabbitmq.producer;

import com.internship.review_service.rabbitmq.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ReviewMessageProducer {

    private final AmqpTemplate amqpTemplate;

    public void sendAddedReviewMessage(String emailTo, Long userId) {
        amqpTemplate.convertAndSend("notifications", "added.review", createAnAddedReviewMessage(emailTo, userId));
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
