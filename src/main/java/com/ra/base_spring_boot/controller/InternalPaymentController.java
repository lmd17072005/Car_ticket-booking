package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.payment.CreateOrderRequest;
import com.ra.base_spring_boot.services.payment.IPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/payments")
@RequiredArgsConstructor
public class InternalPaymentController {
    private final IPaymentService paymentService;

    @PostMapping("/vnpay")
    public ResponseEntity<String> createVnPayOrder(@RequestBody CreateOrderRequest request, HttpServletRequest httpReq) {
        String paymentUrl = paymentService.createVnPayOrder(request.getPaymentId(), request.getAmount(), request.getDescription(), httpReq);
        return ResponseEntity.ok(paymentUrl);
    }
}