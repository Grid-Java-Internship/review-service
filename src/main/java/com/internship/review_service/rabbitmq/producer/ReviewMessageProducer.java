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

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchangeName;

    @Value("${configs.rabbitmq.messaging.forgotPassword}")
    private String forgotPasswordKey;

    /**
     * Sends a message to the "notifications" exchange with the routing key "forgot.password".
     * The message contains the email address extracted from the provided EmailDetails object.
     *
     * @param emailTo the String object representing the email of recipient
     */
    public void sendAddedReviewMessage(String emailTo){
        log.info("{} {}", forgotPasswordKey, exchangeName);

        amqpTemplate.convertAndSend(exchangeName,
                forgotPasswordKey, //temporary for testing purposes
                createAnAddedReviewMessage(emailTo));
    }

    private Message createAnAddedReviewMessage(String emailTo) {
        Message message = new Message();
        message.setTitle("Added review confirmation");
        message.setContent("You have successfully added a review");
        message.setEmailTo(emailTo);

        return message;
    }
}
