package com.ridelink.account_service.service;

import com.ridelink.account_service.dto.*;
import com.ridelink.account_service.model.*;
import com.ridelink.account_service.repository.UserRepository;
import com.ridelink.account_service.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public String registerUser(@NonNull RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email is already in use!");
        }

        Set<Role> roles = request.roles() != null ? request.roles() : Set.of(Role.ROLE_PASSENGER);
        if (roles.contains(Role.ROLE_ADMIN)) {
            throw new RuntimeException("Cannot register as an admin!");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .roles(roles)
                .build();

        userRepository.save(Objects.requireNonNull(user));
        return "User registered successfully!";
    }

    public AuthResponse loginUser(@NonNull LoginRequest request) {
        User user = Objects.requireNonNull(userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found!")));

        if (!user.isActive()) {
            throw new RuntimeException("User account is inactive!");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials!");
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getRoles());
        return new AuthResponse(token, user.getId(), user.getEmail(), 
            user.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet()));
    }
}