package com.ridelink.ridemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalStateException(
            IllegalStateException exception) {

        Map<String, String> error = new HashMap<>();

        error.put("error", "Invalid ride status transition");
        error.put("message", exception.getMessage());

        return error;
    }

        @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleResourceNotFoundException(
            ResourceNotFoundException exception) {

        Map<String, String> error = new HashMap<>();

        error.put("error", "Resource not found");
        error.put("message", exception.getMessage());

        return error;
    }
}