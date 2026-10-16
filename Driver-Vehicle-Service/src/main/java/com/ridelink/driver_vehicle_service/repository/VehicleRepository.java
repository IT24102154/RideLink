package com.ridelink.driver_vehicle_service.repository;

import com.ridelink.driver_vehicle_service.entity.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {
}