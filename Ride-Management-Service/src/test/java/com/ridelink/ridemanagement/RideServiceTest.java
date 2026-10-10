package com.ridelink.ridemanagement;

import com.ridelink.ridemanagement.dto.CreateRideRequest;
import com.ridelink.ridemanagement.model.Ride;
import com.ridelink.ridemanagement.model.RideStatus;
import com.ridelink.ridemanagement.repository.RideRepository;
import com.ridelink.ridemanagement.service.RideService;

import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RideServiceTest {

    @Test
    void createRide_shouldCreateRideWithRequestedStatus() {

        RideRepository rideRepository = mock(RideRepository.class);

        when(rideRepository.count()).thenReturn(0L);

        Ride savedRide = new Ride();
        savedRide.setRideId("RI001");
        savedRide.setPassengerId("P001");
        savedRide.setPickupLocation("Maharagama");
        savedRide.setDestination("Kottawa");
        savedRide.setStatus(RideStatus.REQUESTED);

        when(rideRepository.save(anyRide())).thenReturn(Objects.requireNonNull(savedRide));

        RideService rideService = new RideService(rideRepository);

        CreateRideRequest request = new CreateRideRequest();
        request.setPassengerId("P001");
        request.setPickupLocation("Maharagama");
        request.setDestination("Kottawa");

        Ride result = rideService.createRide(request);

        assertEquals("RI001", result.getRideId());
        assertEquals("P001", result.getPassengerId());
        assertEquals("Maharagama", result.getPickupLocation());
        assertEquals("Kottawa", result.getDestination());
        assertEquals(RideStatus.REQUESTED, result.getStatus());

        verify(rideRepository).save(anyRide());
    }
    @Test
    void updateRideStatus_shouldChangeRequestedToAssigned() {   

    RideRepository rideRepository = mock(RideRepository.class);

    Ride existingRide = new Ride();
    existingRide.setRideId("RI001");
    existingRide.setPassengerId("P001");
    existingRide.setPickupLocation("Maharagama");
    existingRide.setDestination("Kottawa");
    existingRide.setStatus(RideStatus.REQUESTED);

    when(rideRepository.findById("RI001"))
            .thenReturn(java.util.Optional.of(existingRide));

    when(rideRepository.save(anyRide()))
            .thenReturn(existingRide);

    RideService rideService = new RideService(rideRepository);

    Ride result = rideService.updateRideStatus(
            "RI001",
            RideStatus.ASSIGNED
    );

    assertEquals(RideStatus.ASSIGNED, Objects.requireNonNull(result).getStatus());

    verify(rideRepository).save(existingRide);
    }

    @Test
void updateRideStatus_shouldRejectInvalidTransition() {

    RideRepository rideRepository = mock(RideRepository.class);

    Ride existingRide = new Ride();
    existingRide.setRideId("RI001");
    existingRide.setStatus(RideStatus.ASSIGNED);

    when(rideRepository.findById("RI001"))
            .thenReturn(java.util.Optional.of(existingRide));

    RideService rideService = new RideService(rideRepository);

    org.junit.jupiter.api.Assertions.assertThrows(
            IllegalStateException.class,
            () -> rideService.updateRideStatus(
                    "RI001",
                    RideStatus.COMPLETED
            )
    );

    verify(rideRepository, never()).save(anyRide());
    }
    @Test
void getRideById_shouldThrowExceptionWhenRideNotFound() {

    RideRepository rideRepository = mock(RideRepository.class);

    when(rideRepository.findById("RI999"))
            .thenReturn(java.util.Optional.empty());

    RideService rideService = new RideService(rideRepository);

    org.junit.jupiter.api.Assertions.assertThrows(
            com.ridelink.ridemanagement.exception.ResourceNotFoundException.class,
            () -> rideService.getRideById("RI999")
    );
    }
    @Test
void updateRideStatus_shouldCompleteFullRideLifecycle() {

    RideRepository rideRepository = mock(RideRepository.class);

    Ride ride = new Ride();
    ride.setRideId("RI001");
    ride.setStatus(RideStatus.REQUESTED);

    when(rideRepository.findById("RI001"))
            .thenReturn(java.util.Optional.of(ride));

    when(rideRepository.save(anyRide()))
            .thenAnswer(invocation -> invocation.getArgument(0));

    RideService rideService = new RideService(rideRepository);
    org.springframework.web.client.RestTemplate restTemplate = mock(org.springframework.web.client.RestTemplate.class);
    java.util.Map<String, java.math.BigDecimal> mockEstimate = new java.util.HashMap<>();
    mockEstimate.put("estimatedFare", new java.math.BigDecimal("100.00"));
    when(restTemplate.getForObject(anyString(), eq(java.util.Map.class))).thenReturn(mockEstimate);
    when(restTemplate.postForObject(anyString(), isNull(), eq(Object.class))).thenReturn(new Object());
    
    org.springframework.test.util.ReflectionTestUtils.setField(rideService, "restTemplate", restTemplate);
    org.springframework.test.util.ReflectionTestUtils.setField(rideService, "farePaymentServiceUrl", "http://localhost:8083");

    Ride assignedRide =
            rideService.updateRideStatus("RI001", RideStatus.ASSIGNED);

    assertEquals(RideStatus.ASSIGNED, Objects.requireNonNull(assignedRide).getStatus());

    Ride acceptedRide =
            rideService.updateRideStatus("RI001", RideStatus.ACCEPTED);

    assertEquals(RideStatus.ACCEPTED, Objects.requireNonNull(acceptedRide).getStatus());

    Ride inProgressRide =
            rideService.updateRideStatus("RI001", RideStatus.IN_PROGRESS);

    assertEquals(RideStatus.IN_PROGRESS, Objects.requireNonNull(inProgressRide).getStatus());

    Ride completedRide =
            rideService.updateRideStatus("RI001", RideStatus.COMPLETED);

    assertEquals(RideStatus.COMPLETED, Objects.requireNonNull(completedRide).getStatus());

    verify(rideRepository, times(4)).save(ride);
    }

    @Test
void updateRideStatus_shouldCancelRequestedRide() {

    RideRepository rideRepository = mock(RideRepository.class);

    Ride ride = new Ride();
    ride.setRideId("RI002");
    ride.setStatus(RideStatus.REQUESTED);

    when(rideRepository.findById("RI002"))
            .thenReturn(java.util.Optional.of(ride));

    when(rideRepository.save(anyRide()))
            .thenAnswer(invocation -> invocation.getArgument(0));

    RideService rideService = new RideService(rideRepository);

    Ride result =
            rideService.updateRideStatus("RI002", RideStatus.CANCELLED);

    assertEquals(RideStatus.CANCELLED, Objects.requireNonNull(result).getStatus());

    verify(rideRepository).save(ride);
}


    @SuppressWarnings("null")
    @NonNull
    private static Ride anyRide() {
        return any(Ride.class);
    }
}