package com.ra.base_spring_boot.dto.payment;

import com.ra.base_spring_boot.model.constants.PaymentStatus;
import com.ra.base_spring_boot.model.payment.Payment;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {
    private Long id;
    private String userEmail;
    private String providerName;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.amount = payment.getAmount();
        this.status = payment.getStatus();
        this.createdAt = payment.getCreatedAt();
        if (payment.getUser() != null) {
            this.userEmail = payment.getUser().getEmail();
        }
        if (payment.getPaymentProvider() != null) {
            this.providerName = payment.getPaymentProvider().getProviderName();
        }
    }
}