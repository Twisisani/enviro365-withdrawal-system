package com.enviro.assessment.junior.twisisanikhosa.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    Map<String, String> validationErrors
) {
    public ApiErrorResponse(int status, String error, String message) {
        this(LocalDateTime.now(), status, error, message, null);
    }

    public ApiErrorResponse(int status, String error, String message, Map<String, String> validationErrors) {
        this(LocalDateTime.now(), status, error, message, validationErrors);
    }
}