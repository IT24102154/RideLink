package com.ridelink.account_service.dto;

import java.util.Set;

public record AuthResponse(
    String token,
    String id,
    String email,
    Set<String> roles
) {}