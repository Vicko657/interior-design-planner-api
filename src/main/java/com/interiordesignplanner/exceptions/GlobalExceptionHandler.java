package com.interiordesignplanner.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
