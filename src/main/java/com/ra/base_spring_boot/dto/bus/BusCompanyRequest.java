package com.ra.base_spring_boot.dto.bus;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusCompanyRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String image;

    private String descriptions;
}