package com.interiordesignplanner.exceptions;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
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

        /**
         * JwtExceptions:
         * 
         * Handles JwtToken errors
         * 
         */
        @ExceptionHandler({ MalformedJwtException.class, UnsupportedJwtException.class, SignatureException.class,
                        IllegalArgumentException.class })
        public ResponseEntity<ErrorResponse> handleJwtException(
                        Exception e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, e
                                .getMessage(),
                                LocalDateTime.now(), request.getContextPath());

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        /**
         * UserExistsException:
         * 
         * Handles users that already exist in the db
         * based on their mobile number or email address.
         * 
         */
        @ExceptionHandler(UserExistsException.class)
        public ResponseEntity<?> handleExistingUsersException(
                        Exception e, HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, e
                                .getMessage(),
                                LocalDateTime.now(), request.getContextPath());

                // For Admin (Provides more detail)
                if (request.getRequestURI().startsWith("/api/admin/")) {
                        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
                }

                // Generic for User
                return ResponseEntity.badRequest().body("Registration failed");
        }

        /**
         * BadCredentialsException:
         * 
         * Handles when user has bad credentials
         * (Spring Security)
         */
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<String> handleBadCredentialsException(
                        BadCredentialsException e, HttpServletRequest request) {

                return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        /**
         * UsernameNotFoundException:
         * 
         * Handles when users username does not exist
         * (Spring Security)
         */
        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<String> handleUsernameNotFoundException(
                        UsernameNotFoundException e, HttpServletRequest request) {

                return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        /**
         * CompromisedPasswordException:
         * 
         * Handles when users password is compromised
         * (Spring Security)
         */
        @ExceptionHandler(CompromisedPasswordException.class)
        public ResponseEntity<String> handleCompromisedPasswordException(
                        CompromisedPasswordException e, HttpServletRequest request) {

                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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

}
