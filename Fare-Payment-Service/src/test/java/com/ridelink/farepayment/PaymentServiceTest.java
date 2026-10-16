package com.ridelink.farepayment;

import com.ridelink.farepayment.exception.ResourceNotFoundException;
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

    @Test
    void testUpdatePaymentStatus_Success_PendingToCompleted() {
        Payment existingPayment = new Payment();
        existingPayment.setId("12345678-uuid");
        existingPayment.setRideId("R100");
        existingPayment.setAmount(new BigDecimal("50.00"));
        existingPayment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findByRideId("R100")).thenReturn(Optional.of(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);

        Payment updated = paymentService.updatePaymentStatus("R100", PaymentStatus.COMPLETED);

        assertNotNull(updated);
        assertEquals(PaymentStatus.COMPLETED, updated.getStatus());
        assertNotNull(updated.getReceiptId());
        assertTrue(updated.getReceiptId().startsWith("REC-"));
        verify(paymentRepository, times(1)).save(existingPayment);
    }

    @Test
    void testUpdatePaymentStatus_Success_PendingToFailed() {
        Payment existingPayment = new Payment();
        existingPayment.setId("12345678-uuid");
        existingPayment.setRideId("R100");
        existingPayment.setAmount(new BigDecimal("50.00"));
        existingPayment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findByRideId("R100")).thenReturn(Optional.of(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);

        Payment updated = paymentService.updatePaymentStatus("R100", PaymentStatus.FAILED);

        assertNotNull(updated);
        assertEquals(PaymentStatus.FAILED, updated.getStatus());
        verify(paymentRepository, times(1)).save(existingPayment);
    }

    @Test
    void testUpdatePaymentStatus_NotFound() {
        when(paymentRepository.findByRideId("NONEXISTENT")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.updatePaymentStatus("NONEXISTENT", PaymentStatus.COMPLETED);
        });

        assertTrue(ex.getMessage().contains("NONEXISTENT"));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void testUpdatePaymentStatus_InvalidTransition_FromCompleted() {
        Payment payment = new Payment();
        payment.setId("P1");
        payment.setRideId("R100");
        payment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findByRideId("R100")).thenReturn(Optional.of(payment));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            paymentService.updatePaymentStatus("R100", PaymentStatus.FAILED);
        });

        assertEquals("Cannot change status of a COMPLETED payment", ex.getMessage());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void testUpdatePaymentStatus_InvalidTransition_FromFailed() {
        Payment payment = new Payment();
        payment.setId("P1");
        payment.setRideId("R100");
        payment.setStatus(PaymentStatus.FAILED);

        when(paymentRepository.findByRideId("R100")).thenReturn(Optional.of(payment));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            paymentService.updatePaymentStatus("R100", PaymentStatus.COMPLETED);
        });

        assertEquals("Cannot change status of a FAILED payment", ex.getMessage());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void testUpdatePaymentStatus_InvalidTransition_SameStatus() {
        Payment payment = new Payment();
        payment.setId("P1");
        payment.setRideId("R100");
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findByRideId("R100")).thenReturn(Optional.of(payment));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            paymentService.updatePaymentStatus("R100", PaymentStatus.PENDING);
        });

        assertEquals("Payment is already in PENDING status", ex.getMessage());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void testUpdatePaymentStatus_NullRideId() {
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.updatePaymentStatus(null, PaymentStatus.COMPLETED);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.updatePaymentStatus("   ", PaymentStatus.COMPLETED);
        });
    }

    @Test
    void testUpdatePaymentStatus_NullStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.updatePaymentStatus("R100", null);
        });
    }
}
