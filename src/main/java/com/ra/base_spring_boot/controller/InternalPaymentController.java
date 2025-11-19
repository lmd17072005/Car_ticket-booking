package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.payment.CreateOrderRequest;
import com.ra.base_spring_boot.services.payment.IPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/payments")
@RequiredArgsConstructor
@Slf4j
public class InternalPaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/vnpay")
    public ResponseEntity<String> createVnPayOrder(
            @RequestBody CreateOrderRequest request,
            HttpServletRequest httpReq) {

        log.info("Creating VNPay order for Payment ID: {}", request.getPaymentId());

        String paymentUrl = paymentService.createVnPayOrder(
                request.getPaymentId(),
                request.getAmount(),
                request.getDescription(),
                httpReq
        );

        return ResponseEntity.ok(paymentUrl);
    }
}