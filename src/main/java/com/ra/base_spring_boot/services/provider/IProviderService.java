package com.ra.base_spring_boot.services.provider;

import com.ra.base_spring_boot.dto.provider.ProviderRequest;
import com.ra.base_spring_boot.dto.provider.ProviderResponse;

import java.util.List;

public interface IProviderService {
    List<ProviderResponse> findAll();
    ProviderResponse save(ProviderRequest request);
    void delete(Long id);
}