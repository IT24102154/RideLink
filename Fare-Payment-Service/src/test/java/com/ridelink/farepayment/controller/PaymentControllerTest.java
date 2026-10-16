package com.ridelink.farepayment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.farepayment.config.SecurityConfig;
import com.ridelink.farepayment.dto.UpdatePaymentStatusRequest;
import com.ridelink.farepayment.exception.GlobalExceptionHandler;
import com.ridelink.farepayment.exception.ResourceNotFoundException;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.model.PaymentStatus;
import com.ridelink.farepayment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUpdatePaymentStatus_Success() throws Exception {
        Payment payment = new Payment();
        payment.setId("pay-123");
        payment.setRideId("ride-456");
        payment.setAmount(new BigDecimal("75.00"));
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setReceiptId("REC-PAY-123");

        when(paymentService.updatePaymentStatus(eq("ride-456"), eq(PaymentStatus.COMPLETED)))
                .thenReturn(payment);

        UpdatePaymentStatusRequest request = new UpdatePaymentStatusRequest(PaymentStatus.COMPLETED);

        mockMvc.perform(put("/api/payments/ride-456/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pay-123"))
                .andExpect(jsonPath("$.rideId").value("ride-456"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.receiptId").value("REC-PAY-123"));
    }

    @Test
    void testUpdatePaymentStatus_NotFound() throws Exception {
        when(paymentService.updatePaymentStatus(eq("nonexistent-ride"), eq(PaymentStatus.COMPLETED)))
                .thenThrow(new ResourceNotFoundException("Payment not found for ride ID: nonexistent-ride"));

        UpdatePaymentStatusRequest request = new UpdatePaymentStatusRequest(PaymentStatus.COMPLETED);

        mockMvc.perform(put("/api/payments/nonexistent-ride/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Payment not found for ride ID: nonexistent-ride"));
    }

    @Test
    void testUpdatePaymentStatus_InvalidStatus() throws Exception {
        mockMvc.perform(put("/api/payments/ride-456/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"UNRECOGNIZED_STATUS\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatePaymentStatus_MissingStatus() throws Exception {
        mockMvc.perform(put("/api/payments/ride-456/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatePaymentStatus_InvalidStatusTransition() throws Exception {
        when(paymentService.updatePaymentStatus(eq("ride-456"), eq(PaymentStatus.FAILED)))
                .thenThrow(new IllegalStateException("Cannot change status of a COMPLETED payment"));

        UpdatePaymentStatusRequest request = new UpdatePaymentStatusRequest(PaymentStatus.FAILED);

        mockMvc.perform(put("/api/payments/ride-456/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot change status of a COMPLETED payment"));
    }

    @Test
    void testEstimateFare_Success() throws Exception {
        when(paymentService.estimateFare("Colombo", "Kandy")).thenReturn(new BigDecimal("150.00"));

        mockMvc.perform(get("/api/payments/estimate")
                        .param("pickup", "Colombo")
                        .param("destination", "Kandy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estimatedFare").value(150.00));
    }

    @Test
    void testProcessPayment_Success() throws Exception {
        Payment payment = new Payment();
        payment.setId("p-001");
        payment.setRideId("r-001");
        payment.setAmount(new BigDecimal("200.00"));
        payment.setStatus(PaymentStatus.COMPLETED);

        when(paymentService.processPayment(eq("r-001"), eq(new BigDecimal("200.00"))))
                .thenReturn(payment);

        mockMvc.perform(post("/api/payments/process")
                        .param("rideId", "r-001")
                        .param("amount", "200.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p-001"))
                .andExpect(jsonPath("$.rideId").value("r-001"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testGetPayment_Success() throws Exception {
        Payment payment = new Payment();
        payment.setId("p-001");
        payment.setStatus(PaymentStatus.COMPLETED);

        when(paymentService.getPaymentStatus("p-001")).thenReturn(payment);

        mockMvc.perform(get("/api/payments/p-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p-001"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
