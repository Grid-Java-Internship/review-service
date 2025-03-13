package com.internship.review_service.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.List;

@ControllerAdvice
public class ReviewExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        String errorMessage = "Request failed because of an internal problem. " +
                "Please contact support or your administrator. Error: " + ex.getMessage();

        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .messages(List.of(errorMessage))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errorMessages = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .messages(errorMessages)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .messages(errorMessages)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFound(NotFoundException ex){
        String errorMessage = ex.getMessage();

        ExceptionResponse errorResponse = ExceptionResponse
                .builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .messages(List.of(errorMessage))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UnknownUserIdException.class)
    public ResponseEntity<ExceptionResponse> handleUnknownUserId(UnknownUserIdException ex){
        String errorMessage = ex.getMessage();

        ExceptionResponse errorResponse = ExceptionResponse
                .builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .messages(List.of(errorMessage))
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(NoReviewsOnResource.class)
    public ResponseEntity<ExceptionResponse> handleUnknownUserId(NoReviewsOnResource ex){
        String errorMessage = ex.getMessage();

        ExceptionResponse errorResponse = ExceptionResponse
                .builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .messages(List.of(errorMessage))
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(errorResponse);
    }

}
