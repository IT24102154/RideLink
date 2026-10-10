package com.ridelink.ridemanagement.repository;

import com.ridelink.ridemanagement.model.Ride;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RideRepository extends MongoRepository<Ride, String> {

}