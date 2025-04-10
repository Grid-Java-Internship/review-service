package com.internship.review_service.rabbitmq.producer;

import com.internship.review_service.rabbitmq.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewMessageProducerTest {

    @Mock
    private AmqpTemplate amqpTemplate;

    @InjectMocks
    private ReviewMessageProducer reviewMessageProducer;

    @Test
    void testSendAddedReviewMessage() {
        String emailTo = "test@example.com";
        Long userId = 1L;

        reviewMessageProducer.sendAddedReviewMessage(emailTo, userId);

        verify(amqpTemplate, times(1)).convertAndSend(eq("notifications"), eq("addedReview"), any(Message.class));
    }
}
