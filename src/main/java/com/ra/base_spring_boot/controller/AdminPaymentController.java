package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.PageResponse;
import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.payment.PaymentResponse;
import com.ra.base_spring_boot.services.payment.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/payments")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Slf4j
public class AdminPaymentController {

    private final IPaymentService paymentService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<PageResponse<PaymentResponse>>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "createdAt,desc") Pageable pageable) {

        log.info("Admin: Getting all payments, page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<PaymentResponse> paymentPage = paymentService.findAllForAdmin(pageable);
        PageResponse<PaymentResponse> pageResponse = PageResponse.fromPage(paymentPage);

        return ResponseEntity.ok(
                ResponseWrapper.<PageResponse<PaymentResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(pageResponse)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        log.info("Admin: Getting payment by ID: {}", id);

        PaymentResponse payment = paymentService.getPaymentStatus(id);

        return ResponseEntity.ok(
                ResponseWrapper.<PaymentResponse>builder()
                        .status(HttpStatus.OK)
                        .data(payment)
                        .build()
        );
    }
}