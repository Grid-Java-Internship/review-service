package com.internship.review_service.feign.reservation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetReservationResponse {
    private Long id;

    private Long customerId;

    private Long workerId;

    private Long jobId;

    private ReservationStatus status;

    private String title;

    private String description;

    private Double finalPrice;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;
}
