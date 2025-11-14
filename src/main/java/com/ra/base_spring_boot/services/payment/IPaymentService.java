package com.ra.base_spring_boot.services.payment;

import com.ra.base_spring_boot.dto.payment.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ra.base_spring_boot.dto.payment.CreateOrderRequest;
import com.ra.base_spring_boot.dto.payment.ZaloPayCallbackRequest;
import org.springframework.transaction.annotation.Transactional;


public interface IPaymentService {
    Page<PaymentResponse> findAllForAdmin(Pageable pageable);

    @Transactional
    boolean handleZaloPayCallback(String jsonStr);

    String createZaloPayOrder(CreateOrderRequest request);

}