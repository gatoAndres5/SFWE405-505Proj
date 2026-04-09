package com.example.demo.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation failed");

        ApiError body = new ApiError(
                Instant.now().toString(),
                400,
                message,
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(body);
    }

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
        ex.printStackTrace();

        ApiError body = new ApiError(
                Instant.now().toString(),
                500,
                ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(500).body(body);
     }

     @ExceptionHandler(HttpMessageNotReadableException.class)
     public ResponseEntity<ApiError> handleHttpMessageNotReadable(
                HttpMessageNotReadableException ex,
                HttpServletRequest request
        ) {
        ApiError body = new ApiError(
                Instant.now().toString(),
                400,
                "Malformed or invalid request body",
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(body);
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