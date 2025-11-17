package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.services.payment.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentCallbackController {

    private final IPaymentService paymentService;

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<String> handleVnPayIPN(@RequestParam Map<String, String> allParams) {
        log.info("Received VNPAY IPN with params: {}", allParams);
        int result = paymentService.handleVnPayIPN(allParams);

        // Trả về response cho VNPAY theo tài liệu
        if (result == 0) {
            return ResponseEntity.ok("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
        } else if (result == 1) {
            return ResponseEntity.ok("{\"RspCode\":\"01\",\"Message\":\"Order not found\"}");
        } else if (result == 2) {
            return ResponseEntity.ok("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
        } else {
            return ResponseEntity.ok("{\"RspCode\":\"99\",\"Message\":\"Unknown error\"}");
        }
    }
}