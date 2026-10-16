package com.ridelink.farepayment.dto;

import com.ridelink.farepayment.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for updating payment status")
public class UpdatePaymentStatusRequest {

    @NotNull(message = "Payment status is required")
    @Schema(
        description = "Target status of the payment",
        example = "COMPLETED",
        allowableValues = {"PENDING", "COMPLETED", "FAILED"},
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private PaymentStatus status;

    public UpdatePaymentStatusRequest() {
    }

    public UpdatePaymentStatusRequest(PaymentStatus status) {
        this.status = status;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
