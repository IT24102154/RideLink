package com.ridelink.farepayment.controller;

import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.service.PaymentService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/estimate")
    public Map<String, BigDecimal> estimateFare(@RequestParam String pickup, @RequestParam String destination) {
        return Map.of("estimatedFare", paymentService.estimateFare(pickup, destination));
    }

    @PostMapping("/process")
    public Payment processPayment(@RequestParam String rideId, @RequestParam BigDecimal amount) {
        return paymentService.processPayment(rideId, amount);
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable String id) {
        return paymentService.getPaymentStatus(id);
    }
}
