package com.ridelink.account_service.dto;

import com.ridelink.account_service.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record RegisterRequest(
    @NotBlank String name,
    @Email @NotBlank String email,
    @NotBlank String password,
    @NotBlank String phoneNumber,
    Set<Role> roles
) {}