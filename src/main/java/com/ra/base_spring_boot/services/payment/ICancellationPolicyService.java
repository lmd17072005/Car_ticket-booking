package com.ra.base_spring_boot.services.payment;


import com.ra.base_spring_boot.dto.payment.CancellationPolicyRequest;
import com.ra.base_spring_boot.dto.payment.CancellationPolicyResponse;
import java.util.List;

public interface ICancellationPolicyService {
    List<CancellationPolicyResponse> findAll();
    CancellationPolicyResponse findById(Long id);
    CancellationPolicyResponse save(CancellationPolicyRequest request);
    CancellationPolicyResponse update(Long id, CancellationPolicyRequest request);
    void delete(Long id);
}
