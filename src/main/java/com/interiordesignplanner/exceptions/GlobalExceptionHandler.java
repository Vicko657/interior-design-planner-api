package com.interiordesignplanner.exceptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The Global Exception Handler
 * 
 * <p>
 * Handles errors and exceptions that may occur in the system
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

        /**
         * MethodArguementNotValidException:
         * 
         * Handles @Valid @Validated errors
         */
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleFieldValidationErrors(
                        MethodArgumentNotValidException e, HttpServletRequest request) {

                List<String> validationResult = new ArrayList<>();

                e.getBindingResult().getAllErrors().forEach(error -> {
                        String message = error.getDefaultMessage();
                        validationResult.add(message);
                });

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                                "Field Error Exception",
                                validationResult, null,
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * EntityNotFoundException:
         * 
         * Handles entities are not found
         */
        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(ClientNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleEntityNotFound(
                        ClientNotFoundException e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found",
                                "Entity Not Found Exception",
                                null, e
                                                .getMessage(),
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        /**
         * InternalServerErrorException:
         * 
         * Handles internal service errors
         */
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        @ExceptionHandler(InternalServerError.class)
        public ResponseEntity<ErrorResponse> handleInternalServerError(
                        InternalServerError e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Internal Server Error",
                                "Internal Server Error Exception", null, e
                                                .getMessage(),
                                LocalDateTime.now(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

}
