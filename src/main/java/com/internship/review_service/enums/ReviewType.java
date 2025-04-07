package com.internship.review_service.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReviewType implements ReviewEnum {
    USER(0),
    JOB(1);

    private final Integer id;

    @Override
    public int getId() {
        return id;
    }

    public static ReviewType fromId(Integer id) {
        return ReviewEnum.fromId(ReviewType.class, id);
    }
}
