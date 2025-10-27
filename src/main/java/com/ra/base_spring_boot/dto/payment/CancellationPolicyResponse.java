package com.ra.base_spring_boot.dto.payment;

import com.ra.base_spring_boot.model.payment.CancellationPolicy;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CancellationPolicyResponse {
    private Long id;
    private String descriptions;
    private Long routeId;
    private String routeDescription;
    private int cancellationTimeLimit;
    private int refundPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CancellationPolicyResponse(CancellationPolicy policy) {
        this.id = policy.getId();
        this.descriptions = policy.getDescriptions();
        this.cancellationTimeLimit = policy.getCancellationTimeLimit();
        this.refundPercentage = policy.getRefundPercentage();
        this.createdAt = policy.getCreatedAt();
        this.updatedAt = policy.getUpdatedAt();
        if (policy.getRoute() != null) {
            this.routeId = policy.getRoute().getId();
            this.routeDescription = policy.getRoute().getDepartureStation().getName() + " -> " + policy.getRoute().getArrivalStation().getName();
        } else {
            this.routeDescription = "General Policy";
        }
    }
}
