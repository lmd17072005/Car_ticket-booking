package com.ra.base_spring_boot.repository.payment;

import com.ra.base_spring_boot.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, Long>, PagingAndSortingRepository<Payment, Long> {
    Optional<Payment> findByTransactionCode(String transactionCode);

}