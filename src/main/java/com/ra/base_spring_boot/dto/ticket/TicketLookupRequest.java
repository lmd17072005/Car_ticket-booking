package com.ra.base_spring_boot.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketLookupRequest {
    @NotNull(message = "Ticket ID cannot be null")
    private Long ticketId;

    @NotBlank(message = "Phone number cannot be blank")
    private String phone;
}
