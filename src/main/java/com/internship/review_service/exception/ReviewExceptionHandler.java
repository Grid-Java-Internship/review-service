package com.internship.review_service.exception;

import com.internship.review_service.dto.response.ExceptionResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@Slf4j
@ControllerAdvice
public class ReviewExceptionHandler {

    /**
     * Constructs a ResponseEntity containing an ExceptionResponse with the specified status code and messages.
     *
     * @param httpStatus the HTTP status to be set in the response
     * @param messages a list of error messages to be included in the response body
     * @return a ResponseEntity containing the constructed ExceptionResponse
     */
    private ResponseEntity<ExceptionResponse> buildErrorResponse(
            HttpStatus httpStatus,
            List<String> messages
    ) {
        messages.forEach(log::error);

        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .statusCode(httpStatus.value())
                .messages(messages)
                .build();

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    /**
     * Handles ConstraintViolationException, MethodArgumentNotValidException and HttpMessageNotReadableException, and
     * returns a ResponseEntity containing an ExceptionResponse with a status code of 400 (Bad Request) and a list of
     * error messages. The error messages are extracted from the exception as follows:
     * - For ConstraintViolationException, the constraint violation messages are extracted from the constraint violations.
     * - For MethodArgumentNotValidException, the default error messages of the field errors are extracted from the
     *   binding result.
     * - For HttpMessageNotReadableException, the exception message is used as the error message.
     *
     * @param ex the exception to be handled
     * @return a ResponseEntity containing the constructed ExceptionResponse
     */
    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(Exception ex) {

        List<String> errorMessages;

        if (ex instanceof ConstraintViolationException constraintViolationException) {
            errorMessages = constraintViolationException.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();
        }
        else if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            errorMessages = methodArgumentNotValidException.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
        }
        else { // HttpMessageNotReadableException
            errorMessages = List.of(ex.getMessage());
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessages);
    }

    /**
     * Handles ConflictException and returns a ResponseEntity containing an ExceptionResponse with a status code of 409
     * (Conflict) and a list of error messages containing the exception message.
     *
     * @param ex the ConflictException to be handled
     * @return a ResponseEntity containing the constructed ExceptionResponse
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionResponse> handleConflictException(
            ConflictException ex
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, List.of(ex.getMessage()));
    }

    /**
     * Handles NotFoundException and returns a ResponseEntity containing an ExceptionResponse with a status code of 404
     * (Not Found) and a list of error messages containing the exception message.
     *
     * @param ex the NotFoundException to be handled
     * @return a ResponseEntity containing the constructed ExceptionResponse
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(
            NotFoundException ex
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, List.of(ex.getMessage()));
    }

    /**
     * Handles generic {@link Exception}s that are not specifically caught by other handlers.
     *
     * <p>Constructs an error message indicating an internal server error and suggests contacting support.
     * Returns a {@link ResponseEntity} containing an {@link ExceptionResponse} with a status code of 500
     * (Internal Server Error) and a list of error messages that include the exception message.
     *
     * @param ex the {@link Exception} to handle
     * @return a ResponseEntity containing the {@link ExceptionResponse} with a 500 status code
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {

        String errorMessage = "Request failed because of an internal problem. " +
                "Please contact support or your administrator. Error: " + ex.getMessage();

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, List.of(errorMessage));
    }
}
