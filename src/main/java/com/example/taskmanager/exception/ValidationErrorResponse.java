package com.example.taskmanager.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Error response for validation failures with field-specific errors.
 */
@Data
@NoArgsConstructor
public class ValidationErrorResponse extends ErrorResponse {

    private Map<String, String> fieldErrors;

    public ValidationErrorResponse(int status, String message, LocalDateTime timestamp, Map<String, String> fieldErrors) {
        super(status, message, timestamp);
        this.fieldErrors = fieldErrors;
    }

}
