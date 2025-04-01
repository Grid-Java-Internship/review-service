package com.internship.review_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class SimpleMessageResponse {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Integer statusCode;

    private Boolean success;

    private String message;

    private final String timestamp = LocalDateTime.now().format(FORMAT);
}
