package com.ra.base_spring_boot.services.payment;

import com.ra.base_spring_boot.dto.payment.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Map;


public interface IPaymentService {
    Page<PaymentResponse> findAllForAdmin(Pageable pageable);

    PaymentResponse getPaymentStatus(Long paymentId);

    String createVnPayOrder(Long paymentId, BigDecimal amount, String description, HttpServletRequest request);

    int handleVnPayIPN(Map<String, String> vnpayParams);

    PaymentResponse handleVnPayReturn(Map<String, String> vnpayParams);
}