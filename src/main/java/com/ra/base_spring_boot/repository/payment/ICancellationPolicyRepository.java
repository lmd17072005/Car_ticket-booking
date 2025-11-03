package com.ra.base_spring_boot.repository.payment;

import com.ra.base_spring_boot.model.payment.CancellationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ICancellationPolicyRepository extends JpaRepository<CancellationPolicy, Long> {
    List<CancellationPolicy> findByRouteIsNull();
}