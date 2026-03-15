package com.interiordesignplanner.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
// Constructs Error Response
public class ErrorResponse {
    // HTTP Status Code
    private final HttpStatus statusCode;
    // Error Message
    private String message;
    // Time of error
    private final LocalDateTime timeStamp;
    // Path
    private final String path;
}
