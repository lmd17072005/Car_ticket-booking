package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.payment.ZaloPayCallbackRequest;
import com.ra.base_spring_boot.services.payment.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentCallbackController {

    private final IPaymentService paymentService;

    @PostMapping("/zalopay-callback")
    public ResponseEntity<Map<String, Object>> handleZaloPayCallback(@RequestBody String jsonStr) {
        log.info("Received ZaloPay callback data");

        boolean isSuccess = paymentService.handleZaloPayCallback(jsonStr);

        if (isSuccess) {
            log.info("Callback processed successfully. Responding to ZaloPay with success code.");
            return ResponseEntity.ok(Map.of("return_code", 1, "return_message", "success"));
        } else {
            log.warn("Callback processing failed. Responding to ZaloPay with error code.");
            return ResponseEntity.ok(Map.of("return_code", -1, "return_message", "mac not equal or error"));
        }
    }
}