package com.ridelink.farepayment.service;

import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.model.PaymentStatus;
import com.ridelink.farepayment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public BigDecimal estimateFare(String pickup, String destination) {
        // Deterministic simulation: length of pickup + destination strings * base rate
        int distanceSim = pickup.length() + destination.length();
        return BigDecimal.valueOf(distanceSim * 15.50).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public Payment processPayment(String rideId, BigDecimal amount) {
        if (paymentRepository.existsByRideId(rideId)) {
            throw new IllegalStateException("Payment already processed for this ride");
        }
        
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        payment.setRideId(rideId);
        payment.setAmount(amount);
        // Simulate immediate success
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setReceiptId("REC-" + payment.getId().substring(0, 8).toUpperCase());
        
        return paymentRepository.save(payment);
    }

    public Payment getPaymentStatus(String paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}
