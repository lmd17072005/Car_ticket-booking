package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.payment.CreateOrderRequest;
import com.ra.base_spring_boot.services.payment.IPaymentService;
import jakarta.validation.Valid;
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

    @PostMapping("/zalopay-order")
    public ResponseEntity<String> createZaloPayOrder(@Valid @RequestBody CreateOrderRequest request) {
        String orderUrl = paymentService.createZaloPayOrder(request);
        return ResponseEntity.ok(orderUrl);
    }
}