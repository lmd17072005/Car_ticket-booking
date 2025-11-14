package com.ra.base_spring_boot.services.provider;

import com.ra.base_spring_boot.dto.provider.ProviderRequest;
import com.ra.base_spring_boot.dto.provider.ProviderResponse;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.payment.PaymentProvider;
import com.ra.base_spring_boot.repository.payment.IPaymentProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements IProviderService {
    private final IPaymentProviderRepository providerRepository;

    @Override
    public List<ProviderResponse> findAll() {
        return providerRepository.findAll().stream().map(ProviderResponse::new).collect(Collectors.toList());
    }

    @Override
    public ProviderResponse save(ProviderRequest request) {
        PaymentProvider provider = new PaymentProvider();
        provider.setProviderName(request.getProviderName());
        provider.setProviderType(request.getProviderType());
        provider.setApiEndpoint(request.getApiEndpoint());
        return new ProviderResponse(providerRepository.save(provider));
    }

    @Override
    public void delete(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new HttpNotFound("Không tìm thấy nhà cung cấp thanh toán với ID: " + id);
        }
        providerRepository.deleteById(id);
    }
}