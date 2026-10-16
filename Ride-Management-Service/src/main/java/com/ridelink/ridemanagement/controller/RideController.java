package com.ridelink.ridemanagement.controller;

import com.ridelink.ridemanagement.dto.CreateRideRequest;
import com.ridelink.ridemanagement.model.Ride;
import com.ridelink.ridemanagement.model.RideStatus;
import com.ridelink.ridemanagement.service.RideService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.lang.NonNull;

import java.util.List;

@RestController
@Tag(
        name = "Ride Management",
        description = "APIs for creating, retrieving, and managing ride lifecycle status"
)
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping("/api/rides")
    public Ride createRide(@NonNull @Valid @RequestBody CreateRideRequest request) {
        return rideService.createRide(request);
    }

    @GetMapping("/api/rides")
    public List<Ride> getAllRides() {
        return rideService.getAllRides();
    }

    @GetMapping("/api/rides/{rideId}")
    public Ride getRideById(@NonNull @PathVariable String rideId) {
        return rideService.getRideById(rideId);
    }

    @PutMapping("/api/rides/{rideId}/status")
    public Ride updateRideStatus( 
            @NonNull @PathVariable String rideId,
            @NonNull @RequestParam RideStatus status) {
        return rideService.updateRideStatus(rideId, status);
    }

    @PutMapping("/api/rides/{rideId}/assign")
    public Ride assignDriver(@NonNull @PathVariable String rideId) {
        return rideService.assignDriver(rideId);
    }
}