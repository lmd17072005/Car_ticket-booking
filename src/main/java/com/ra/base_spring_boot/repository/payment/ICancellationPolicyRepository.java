package com.ra.base_spring_boot.repository.payment;

import com.ra.base_spring_boot.model.Bus.Route;
import com.ra.base_spring_boot.model.payment.CancellationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

@Repository
public interface ICancellationPolicyRepository extends JpaRepository<CancellationPolicy, Long> {
    Optional<CancellationPolicy> findByRoute(Route route);

    @Query("SELECT cp FROM CancellationPolicy cp WHERE cp.route IS NULL")
    Optional<CancellationPolicy> findGeneralPolicy();
}
