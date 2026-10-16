package com.ridelink.ridemanagement.service;

import com.ridelink.ridemanagement.model.Ride;
import com.ridelink.ridemanagement.model.RideStatus;
import com.ridelink.ridemanagement.repository.RideRepository;
import com.ridelink.ridemanagement.dto.CreateRideRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.ridelink.ridemanagement.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final org.springframework.web.client.RestTemplate restTemplate;

    @org.springframework.beans.factory.annotation.Value("${driver.vehicle.service.url:http://localhost:8081}")
    private String driverVehicleServiceUrl;

    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
        this.restTemplate = new org.springframework.web.client.RestTemplate();
    }

    public static class DriverDto {
        public String id;
        public String name;
    }

    public Ride assignDriver(@NonNull String rideId) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new ResourceNotFoundException("Ride not found with ID: " + rideId));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new IllegalStateException("Ride must be in REQUESTED state to assign a driver.");
        }

        String location = ride.getPickupLocation();
        String getUrl = driverVehicleServiceUrl + "/drivers/available?location=" + location;
        
        DriverDto[] drivers;
        try {
            drivers = restTemplate.getForObject(getUrl, DriverDto[].class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch drivers from Driver-Vehicle Service", e);
        }

        if (drivers == null || drivers.length == 0) {
            throw new IllegalStateException("No eligible drivers available for location: " + location);
        }

        // Deterministic selection: first available driver
        String selectedDriverId = drivers[0].id;

        String reserveUrl = driverVehicleServiceUrl + "/drivers/" + selectedDriverId + "/reserve";
        try {
            restTemplate.put(reserveUrl, null);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            throw new IllegalStateException("Driver assignment conflict or driver not available", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reserve driver", e);
        }

        ride.setDriverId(selectedDriverId);
        ride.setStatus(RideStatus.ASSIGNED);
        return rideRepository.save(ride);
    }

    public Ride createRide(@NonNull CreateRideRequest request) {

    Ride ride = new Ride();

    ride.setRideId("RI-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());

    ride.setPassengerId(request.getPassengerId());
    ride.setPickupLocation(request.getPickupLocation());
    ride.setDestination(request.getDestination());
    ride.setStatus(RideStatus.REQUESTED);

    return rideRepository.save(ride);
}

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public Ride getRideById(@NonNull String rideId) {
    return rideRepository.findById(rideId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Ride not found with ID: " + rideId
                    )
            );
}

    @org.springframework.beans.factory.annotation.Value("${fare.payment.service.url:http://localhost:8083}")
    private String farePaymentServiceUrl;

    public Ride updateRideStatus(@NonNull String rideId, @NonNull RideStatus newStatus) {
    Ride ride = rideRepository.findById(rideId)
        .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Ride not found with ID: " + rideId
                )
        );

    RideStatus currentStatus = ride.getStatus();

    if (!isValidTransition(currentStatus, newStatus)) {
        throw new IllegalStateException(
                "Invalid ride status transition from "
                        + currentStatus + " to " + newStatus
        );
    }

    if (newStatus == RideStatus.COMPLETED) {
        try {
            String estimateUrl = farePaymentServiceUrl + "/api/payments/estimate?pickup=" + ride.getPickupLocation() + "&destination=" + ride.getDestination();
            java.util.Map estimate = restTemplate.getForObject(estimateUrl, java.util.Map.class);
            Object fareObj = estimate.get("estimatedFare");
            String processUrl = farePaymentServiceUrl + "/api/payments/process?rideId=" + ride.getRideId() + "&amount=" + fareObj.toString();
            restTemplate.postForObject(processUrl, null, Object.class);
        } catch (Exception e) {
            throw new RuntimeException("Payment processing failed. Ride cannot be completed.", e);
        }
    }

    ride.setStatus(newStatus);

    return rideRepository.save(ride);
}

private boolean isValidTransition(RideStatus currentStatus, RideStatus newStatus) {

    if (currentStatus == RideStatus.REQUESTED) {
        return newStatus == RideStatus.ASSIGNED
                || newStatus == RideStatus.CANCELLED;
    }

    if (currentStatus == RideStatus.ASSIGNED) {
        return newStatus == RideStatus.ACCEPTED
                || newStatus == RideStatus.CANCELLED;
    }

    if (currentStatus == RideStatus.ACCEPTED) {
        return newStatus == RideStatus.IN_PROGRESS
                || newStatus == RideStatus.CANCELLED;
    }

    if (currentStatus == RideStatus.IN_PROGRESS) {
        return newStatus == RideStatus.COMPLETED
                || newStatus == RideStatus.CANCELLED;
    }

    return false;
    }
}