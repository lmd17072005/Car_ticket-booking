package com.ra.base_spring_boot.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CreateOrderRequest {
    @NotNull
    private Long paymentId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private String description;
}