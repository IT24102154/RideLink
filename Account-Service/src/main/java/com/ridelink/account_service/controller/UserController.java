package com.ridelink.account_service.controller;

import com.ridelink.account_service.dto.UpdateProfileRequest;
import com.ridelink.account_service.model.Role;
import com.ridelink.account_service.model.User;
import com.ridelink.account_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.Map;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        User user = userService.getCurrentUserProfile(authentication.getName());
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody UpdateProfileRequest request) {
        User updated = userService.updateProfile(authentication.getName(), request);
        updated.setPassword(null);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestParam boolean active) {
        User updated = userService.updateAccountStatus(id, active);
        updated.setPassword(null);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/roles")
    public ResponseEntity<?> updateRoles(@PathVariable String id, @RequestBody Set<Role> roles) {
        User updated = userService.updateRoles(id, roles);
        updated.setPassword(null);
        return ResponseEntity.ok(updated);
    }
}
