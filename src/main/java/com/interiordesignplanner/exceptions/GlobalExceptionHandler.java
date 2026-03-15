package com.interiordesignplanner.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * The Global Exception Handler
 * 
 * <p>
 * Handles errors and exceptions that may occur in the system
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        /**
         * MethodArguementNotValidException:
         * 
         * Handles @Valid field errors
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleFieldValidationErrors(
                        MethodArgumentNotValidException e) {

                Map<String, String> errors = new HashMap<>();
                List<FieldError> fieldErrorResult = e.getBindingResult().getFieldErrors();
                fieldErrorResult.forEach(error -> {
                        errors.put(error.getField(), error.getDefaultMessage());
                });

                return ResponseEntity.badRequest().body(errors);
        }

        /**
         * EntityNotFoundException:
         * 
         * Handles entities are not found
         */
        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleEntityNotFound(
                        EntityNotFoundException e, WebRequest webRequest) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e
                                .getMessage(),
                                LocalDateTime.now(), webRequest.getDescription(false));

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        /**
         * InternalServerErrorException:
         * 
         * Handles internal service errors
         */
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleInternalServerError(
                        Exception e, WebRequest webRequest) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                e
                                                .getMessage(),
                                LocalDateTime.now(), webRequest.getDescription(false));

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        /**
         * ConstraintViolationException:
         * 
         * Handles @Validated class-level errors
         */
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<Map<String, String>> handleConstraintViolation(
                        ConstraintViolationException e) {

                Map<String, String> errors = new HashMap<>();
                Set<ConstraintViolation<?>> constraintViolationSet = e.getConstraintViolations();
                constraintViolationSet.forEach(constraintViolation -> {
                        errors.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
                });

                return ResponseEntity.badRequest().body(errors);
        }

}
