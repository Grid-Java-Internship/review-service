package com.internship.review_service.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Status implements ReviewEnum {
    RECEIVED(0),
    IN_REVIEW(1),
    ACCEPTED(2),
    REJECTED(3);

    private final Integer id;

    @Override
    public int getId() {
        return id;
    }

    public static Status fromId(Integer id) {
        return ReviewEnum.fromId(Status.class, id);
    }
}
