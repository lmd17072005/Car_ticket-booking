package com.ra.base_spring_boot.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelTicketRequest {
    @NotBlank(message = "Description reason is required")
    private String  descriptions;
}