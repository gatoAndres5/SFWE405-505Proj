package com.example.demo.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {
        int status = ex.getStatusCode().value();
        ApiError body = new ApiError(
                Instant.now().toString(),
                status,
                ex.getReason(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        ApiError body = new ApiError(
                Instant.now().toString(),
                403,
                "Forbidden",
                request.getRequestURI()
        );
        return ResponseEntity.status(403).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ApiError body = new ApiError(
                Instant.now().toString(),
                400,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(400).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(
            Exception ex,
            HttpServletRequest request
    ) {
        ApiError body = new ApiError(
                Instant.now().toString(),
                500,
                "Internal server error",
                request.getRequestURI()
        );
        return ResponseEntity.status(500).body(body);
    }

    public static class ApiError {
        public String timestamp;
        public int status;
        public String message;
        public String path;

        public ApiError(String timestamp, int status, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.message = message;
            this.path = path;
        }
    }
}