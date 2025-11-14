package com.ra.base_spring_boot.dto.provider;

import com.ra.base_spring_boot.model.constants.ProviderType;
import com.ra.base_spring_boot.model.payment.PaymentProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderResponse {
    private Long id;
    private String providerName;
    private ProviderType providerType;
    private String apiEndpoint;

    public ProviderResponse(PaymentProvider provider) {
        this.id = provider.getId();
        this.providerName = provider.getProviderName();
        this.providerType = provider.getProviderType();
        this.apiEndpoint = provider.getApiEndpoint();
    }
}