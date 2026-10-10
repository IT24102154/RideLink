package com.ridelink.driver_vehicle_service.controller;

import com.ridelink.driver_vehicle_service.entity.Driver;
import com.ridelink.driver_vehicle_service.service.DriverService;

import jakarta.validation.Valid;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @GetMapping("/available")
    public List<Driver> getAvailableDrivers(@NonNull @RequestParam String location) {
        return driverService.getAvailableDrivers(location);
    }

    @PostMapping
    public Driver createDriver(@NonNull @Valid @RequestBody Driver driver) {
        return driverService.createDriver(driver);
    }

    @PutMapping("/{id}")
    public Driver updateDriver(@NonNull @PathVariable String id, @NonNull @RequestBody Driver driver) {
        return driverService.updateDriver(id, driver);
    }

    @DeleteMapping("/{id}")
    public void deleteDriver(@NonNull @PathVariable String id) {
        driverService.deleteDriver(id);
    }

    @PutMapping("/{driverId}/vehicle/{vehicleId}")
    public Driver assignVehicle(
        @NonNull @PathVariable String driverId,
        @NonNull @PathVariable String vehicleId) {

        return driverService.assignVehicle(driverId, vehicleId);
    }

    @PutMapping("/{id}/reserve")
    public Driver reserveDriver(@NonNull @PathVariable String id) {
        return driverService.reserveDriver(id);
    }

}