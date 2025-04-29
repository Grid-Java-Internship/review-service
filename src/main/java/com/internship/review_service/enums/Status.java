package com.internship.review_service.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Status implements ReviewEnum {
    RECEIVED(0),
    IN_REVIEW(1),
    ACCEPTED(2),
    REJECTED(3),
    DELETED(4),
    DISABLED(5);

    private final Integer id;

    @Override
    public int getId() {
        return id;
    }

    public boolean equals(Status status) {
        return this.id == status.getId();
    }

    public static Status fromId(Integer id) {
        return ReviewEnum.fromId(Status.class, id);
    }
}
