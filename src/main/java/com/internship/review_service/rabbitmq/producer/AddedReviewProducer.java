package com.internship.review_service.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AddedReviewProducer {

    private final AmqpTemplate amqpTemplate;

    public void sendMessage(String message){
        amqpTemplate.convertAndSend("notifications","forgot.password",message);
    }
}
