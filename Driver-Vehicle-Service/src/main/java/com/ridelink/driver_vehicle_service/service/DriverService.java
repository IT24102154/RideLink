package com.ridelink.driver_vehicle_service.service;

import com.ridelink.driver_vehicle_service.entity.Driver;
import com.ridelink.driver_vehicle_service.exception.ResourceNotFoundException;
import com.ridelink.driver_vehicle_service.repository.DriverRepository;
import com.ridelink.driver_vehicle_service.entity.Vehicle;
import com.ridelink.driver_vehicle_service.repository.VehicleRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    private final VehicleRepository vehicleRepository;

    public DriverService(DriverRepository driverRepository, VehicleRepository vehicleRepository) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver createDriver(@NonNull Driver driver) {
        driver.setVehicle(null);
        return driverRepository.save(driver);
    }

    public Driver updateDriver(@NonNull String id, @NonNull Driver driver) {
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + id));

        existingDriver.setName(driver.getName());
        existingDriver.setPhone(driver.getPhone());
        existingDriver.setAvailable(driver.isAvailable());
        existingDriver.setLocation(driver.getLocation());

        return driverRepository.save(existingDriver);
    }

    public void deleteDriver(@NonNull String id) {
        driverRepository.deleteById(id);
    }

    public List<Driver> getAvailableDrivers(@NonNull String location) {
        return driverRepository.findByAvailableTrue().stream()
                .filter(d -> d.getLocation() != null
                          && location.equalsIgnoreCase(d.getLocation()))
                .collect(java.util.stream.Collectors.toList());
    }

    public Driver reserveDriver(@NonNull String driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
        if (!driver.isAvailable()) {
            throw new IllegalStateException("Driver is not available for a ride.");
        }
        driver.setAvailable(false);
        return driverRepository.save(driver);
    }

    public Driver assignVehicle(@NonNull String driverId, @NonNull String vehicleId) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        if (!vehicle.isAvailable()) {
            throw new IllegalStateException("Vehicle is not available for assignment");
        }

        if (driver.getVehicle() != null) {
             Vehicle oldVehicle = driver.getVehicle();
             oldVehicle.setAvailable(true);
             vehicleRepository.save(oldVehicle);
        }

        driver.setVehicle(vehicle);

        vehicle.setAvailable(false);
        vehicleRepository.save(vehicle);

        return driverRepository.save(driver);
    }


}