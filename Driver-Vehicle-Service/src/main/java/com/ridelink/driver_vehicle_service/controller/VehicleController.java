package com.ridelink.driver_vehicle_service.controller;

import com.ridelink.driver_vehicle_service.entity.Vehicle;
import com.ridelink.driver_vehicle_service.service.VehicleService;

import jakarta.validation.Valid;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @PostMapping
    public Vehicle createVehicle(@NonNull @Valid @RequestBody Vehicle vehicle) {
        return vehicleService.createVehicle(vehicle);
    }

    @PutMapping("/{id}")
    public Vehicle updateVehicle(
            @NonNull @PathVariable String id,
            @NonNull @RequestBody Vehicle vehicle) {

        return vehicleService.updateVehicle(id, vehicle);
    }

    @PutMapping("/{id}/location")
    public Vehicle updateLocation(
            @NonNull @PathVariable String id,
            @NonNull @RequestParam String location) {

        return vehicleService.updateLocation(id, location);
    }

    @DeleteMapping("/{id}")
    public void deleteVehicle(@NonNull @PathVariable String id) {
        vehicleService.deleteVehicle(id);
    }
}