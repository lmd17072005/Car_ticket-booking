package com.ra.base_spring_boot.dto.ticket;

import com.ra.base_spring_boot.model.constants.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketRequest {
    @NotNull(message = "Schedule ID cannot be null")
    private Long scheduleId;

    @NotNull(message = "Seat ID cannot be null")
    private Long seatId;

    @NotNull(message = "Payment Method cannot be null")
    private PaymentMethod paymentMethod;
}
