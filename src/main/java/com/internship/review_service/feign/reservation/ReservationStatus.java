package com.internship.review_service.feign.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    PENDING_WORKER_APPROVAL(1),
    APPROVED(2),
    REJECTED(3),
    PENDING_CUSTOMER_APPROVAL(4),
    CANCELED_BY_WORKER(5),
    CANCELED_BY_CUSTOMER(6),
    APPROVED_BY_WORKER(7),
    APPROVED_BY_CUSTOMER(8),
    REJECTED_BY_CUSTOMER(9),
    FINISHED(10);

    private final int id;

    public static ReservationStatus fromId(int id) {
        return Arrays.stream(values())
                .filter(status -> status.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation status id: " + id));
    }
}