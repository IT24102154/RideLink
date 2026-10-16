package com.ridelink.farepayment.controller;

import com.ridelink.farepayment.dto.UpdatePaymentStatusRequest;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Management", description = "APIs for fare estimation, payment processing, and status management")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "", description = "Calculates estimated fare based on pickup and destination")
    @GetMapping("/estimate")
    public Map<String, BigDecimal> estimateFare(@RequestParam String pickup, @RequestParam String destination) {
        return Map.of("estimatedFare", paymentService.estimateFare(pickup, destination));
    }

    @Operation(summary = "", description = "Processes an initial payment for a ride")
    @PostMapping("/process")
    public Payment processPayment(@RequestParam String rideId, @RequestParam BigDecimal amount) {
        return paymentService.processPayment(rideId, amount);
    }

    @Operation(summary = "", description = "Retrieves payment details by payment ID")
    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable String id) {
        return paymentService.getPaymentStatus(id);
    }

    @Operation(
        summary = "",
        description = "Updates the status of a payment associated with the given rideId. Valid transitions are from PENDING to COMPLETED or FAILED."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Payment status updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Payment.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid status value or invalid status transition",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Cannot change status of a COMPLETED payment"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Payment not found for the given rideId",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Payment not found for ride ID: RI-12345678"))
        )
    })
    @PutMapping("/{rideId}/status")
    public Payment updatePaymentStatus(
            @Parameter(description = "ID of the ride associated with the payment", example = "RI-12345678", required = true)
            @PathVariable String rideId,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {
        if (request == null || request.getStatus() == null) {
            throw new IllegalArgumentException("Payment status is required");
        }
        return paymentService.updatePaymentStatus(rideId, request.getStatus());
    }
}
