package com.byt.freeEdu.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.byt.freeEdu.config.AppConfig;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalErrorHandler {

    private final AppConfig appConfig;

    @Autowired
    public GlobalErrorHandler(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(
            EntityNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, "Entity not found", ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid input", ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", ex);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message,
                                                                    Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);

        if (appConfig.isDebugMode()) {
            response.put("exception", ex.getClass().getSimpleName());
            response.put("details", ex.getMessage());
        }

        return ResponseEntity.status(status).body(response);
    }
}
