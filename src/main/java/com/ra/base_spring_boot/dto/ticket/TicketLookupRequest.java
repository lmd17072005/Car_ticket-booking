package com.ra.base_spring_boot.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketLookupRequest {
    @NotBlank(message = "Mã vé không được để trống")
    private String ticketCode;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;
}
