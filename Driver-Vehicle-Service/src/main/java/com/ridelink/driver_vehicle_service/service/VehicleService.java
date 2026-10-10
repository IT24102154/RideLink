package com.ridelink.driver_vehicle_service.service;

import com.ridelink.driver_vehicle_service.entity.Vehicle;
import com.ridelink.driver_vehicle_service.exception.ResourceNotFoundException;
import com.ridelink.driver_vehicle_service.repository.VehicleRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getAllVehicles() {

        List<Vehicle> vehicles = vehicleRepository.findAll();

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getDriver() == null) {
                vehicle.setAvailable(true);
            } else {
                vehicle.setAvailable(false);
            }
        }

        return vehicles;
    }

    public Vehicle createVehicle(@NonNull Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(@NonNull String id, @NonNull Vehicle vehicle) {

        Vehicle existingVehicle =
                vehicleRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));

        existingVehicle.setVehicleNumber(vehicle.getVehicleNumber());
        existingVehicle.setVehicleType(vehicle.getVehicleType());
        existingVehicle.setLocation(vehicle.getLocation());

        return vehicleRepository.save(existingVehicle);
    }

    public Vehicle updateLocation(@NonNull String id, @NonNull String location) {

        Vehicle vehicle =
                vehicleRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));

        vehicle.setLocation(location);

        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(@NonNull String id) {
        vehicleRepository.deleteById(id);
    }
}