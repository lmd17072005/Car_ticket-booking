package com.ra.base_spring_boot.repository.payment;

import com.ra.base_spring_boot.model.payment.PaymentProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPaymentProviderRepository extends JpaRepository<PaymentProvider, Long> {
}