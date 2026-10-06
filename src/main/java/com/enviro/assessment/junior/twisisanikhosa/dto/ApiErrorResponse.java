package com.enviro.assessment.junior.twisisanikhosa.dto;

import java.time.LocalDateTime;
import java.util.Map;

// Standardized API error response
public record ApiErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    Map<String, String> validationErrors
) {
    // Constructor without timestamp (auto-set to now)
    public ApiErrorResponse(int status, String error, String message) {
        this(LocalDateTime.now(), status, error, message, null);
    }

    // Constructor with validation errors
    public ApiErrorResponse(int status, String error, String message, Map<String, String> validationErrors) {
        this(LocalDateTime.now(), status, error, message, validationErrors);
    }
}