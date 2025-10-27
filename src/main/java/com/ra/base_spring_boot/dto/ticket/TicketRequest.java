package com.ra.base_spring_boot.dto.ticket;

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
}
