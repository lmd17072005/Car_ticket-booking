package com.ra.base_spring_boot.dto.provider;

import com.ra.base_spring_boot.model.constants.ProviderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderRequest {
    @NotBlank(message = "Payment provider cannot be blank")
    private String providerName;

    @NotNull(message = "Provider type cannot be null")
    private ProviderType providerType;

    private String apiEndpoint;
}