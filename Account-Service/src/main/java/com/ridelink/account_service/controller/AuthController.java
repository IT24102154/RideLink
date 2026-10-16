package com.ridelink.account_service.controller;

import com.ridelink.account_service.dto.*;
import com.ridelink.account_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginUser(request));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<java.util.Map<String, String>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage() != null ? e.getMessage() : "Error occurred"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<java.util.Map<String, String>> handleGeneralException(Exception e) {
        return ResponseEntity.internalServerError().body(java.util.Map.of("message", e.getMessage() != null ? e.getMessage() : "Internal Server Error"));
    }
}