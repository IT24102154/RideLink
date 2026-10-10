package com.ridelink.farepayment;

import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.model.PaymentStatus;
import com.ridelink.farepayment.repository.PaymentRepository;
import com.ridelink.farepayment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void testEstimateFare() {
        BigDecimal estimate = paymentService.estimateFare("A", "B");
        assertEquals(new BigDecimal("31.00"), estimate);
    }

    @Test
    void testProcessPayment_Success() {
        when(paymentRepository.existsByRideId("R1")).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);
        
        Payment payment = paymentService.processPayment("R1", new BigDecimal("100.00"));
        assertNotNull(payment);
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
        assertEquals(new BigDecimal("100.00"), payment.getAmount());
    }

    @Test
    void testProcessPayment_Duplicate() {
        when(paymentRepository.existsByRideId("R1")).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment("R1", new BigDecimal("100.00"));
        });
    }

    @Test
    void testGetPaymentStatus_NotFound() {
        when(paymentRepository.findById("P1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> {
            paymentService.getPaymentStatus("P1");
        });
    }
}
