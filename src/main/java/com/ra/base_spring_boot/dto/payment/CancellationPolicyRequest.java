package com.ra.base_spring_boot.dto.payment;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancellationPolicyRequest {
    @NotBlank(message = "Description cannot be blank")
    private String descriptions;

    private Long routeId;

    @NotNull(message = "Cancellation time limit cannot be null")
    @Min(value = 0, message = "Cancellation time limit must be non-negative")
    private Integer cancellationTimeLimit;

    @NotNull(message = "Refund percentage cannot be null")
    @Min(value = 0, message = "Refund percentage must be more than or equal to 0")
    @Max(value = 100, message = "Refund percentage must be less than or equal to 100")
    private Integer refundPercentage;
}
