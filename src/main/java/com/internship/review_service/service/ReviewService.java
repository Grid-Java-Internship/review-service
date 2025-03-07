package com.internship.review_service.service;

import com.internship.review_service.rabbitmq.producer.AddedReviewProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final AddedReviewProducer addedReviewProducer;

    public void addNewReview(){
        addedReviewProducer.sendMessage("BLA");
    }

}
