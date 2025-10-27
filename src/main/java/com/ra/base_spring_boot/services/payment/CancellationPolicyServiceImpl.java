package com.ra.base_spring_boot.services.payment;

import com.ra.base_spring_boot.dto.payment.CancellationPolicyRequest;
import com.ra.base_spring_boot.dto.payment.CancellationPolicyResponse;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Route;
import com.ra.base_spring_boot.model.payment.CancellationPolicy;
import com.ra.base_spring_boot.repository.payment.ICancellationPolicyRepository;
import com.ra.base_spring_boot.repository.route.IRouteRepository;
import com.ra.base_spring_boot.services.payment.ICancellationPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CancellationPolicyServiceImpl implements ICancellationPolicyService {
    private final ICancellationPolicyRepository cancellationPolicyRepository;
    private final IRouteRepository routeRepository;

    @Override
    public List<CancellationPolicyResponse> findAll() {
        return cancellationPolicyRepository.findAll().stream()
                .map(CancellationPolicyResponse::new).collect(Collectors.toList());
    }

    @Override
    public CancellationPolicyResponse findById(Long id) {
        CancellationPolicy policy = cancellationPolicyRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Not found cancellation policy with id: " + id));
        return new CancellationPolicyResponse(policy);
    }


    @Override
    public CancellationPolicyResponse save(CancellationPolicyRequest request) {
        CancellationPolicy policy = mapRequestToEntity(new CancellationPolicy(), request);
        return new CancellationPolicyResponse(cancellationPolicyRepository.save(policy));
    }

    @Override
    public CancellationPolicyResponse update(Long id, CancellationPolicyRequest request) {

        CancellationPolicy existingPolicy = cancellationPolicyRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy chính sách với ID: " + id));
        CancellationPolicy updatedPolicy = mapRequestToEntity(existingPolicy, request);
        return new CancellationPolicyResponse(cancellationPolicyRepository.save(updatedPolicy));
    }

    @Override
    public void delete(Long id) {
        if (!cancellationPolicyRepository.existsById(id)) {
            throw new HttpNotFound("Cancellation Policy not found with id : " + id);
        } else {
            cancellationPolicyRepository.deleteById(id);
        }
    }

    private CancellationPolicy mapRequestToEntity(CancellationPolicy policy, CancellationPolicyRequest request) {
        policy.setDescriptions(request.getDescriptions());
        policy.setCancellationTimeLimit(request.getCancellationTimeLimit());
        policy.setRefundPercentage(request.getRefundPercentage());

        if (request.getRouteId() != null) {
            Route route = routeRepository.findById(request.getRouteId())
                    .orElseThrow(() -> new HttpNotFound("Route not found with id : " + request.getRouteId()));
            policy.setRoute(route);
        } else {
            policy.setRoute(null);
    }
        return policy;
    }

}
