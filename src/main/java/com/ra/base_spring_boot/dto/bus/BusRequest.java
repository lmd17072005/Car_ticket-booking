package com.ra.base_spring_boot.dto.bus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusRequest {
    @NotBlank(message = "Bus name is required")
    private String name;

    private String descriptions;

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be more than 0")
    private Integer capacity;

    @NotNull(message = "Company ID is required")
    private Long companyId;
}
