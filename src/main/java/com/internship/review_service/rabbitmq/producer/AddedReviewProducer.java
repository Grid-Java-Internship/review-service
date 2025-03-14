package com.internship.review_service.rabbitmq.producer;

import com.internship.review_service.rabbitmq.EmailDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AddedReviewProducer {

    private final AmqpTemplate amqpTemplate;

    /**
     * Sends a message to the "notifications" exchange with the routing key "forgot.password".
     * The message contains the email address extracted from the provided EmailDetails object.
     *
     * @param emailDetails the EmailDetails object containing the email address to be sent
     */
    public void sendMessage(EmailDetails emailDetails){
        amqpTemplate.convertAndSend("notifications",
                "forgot.password",
                emailDetails.getEmail());
    }
}
