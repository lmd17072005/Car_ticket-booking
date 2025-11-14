package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.PageResponse;
import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.payment.PaymentResponse;
import com.ra.base_spring_boot.services.payment.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/payments")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminPaymentController {

    private final IPaymentService paymentService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<PageResponse<PaymentResponse>>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "createdAt,desc") Pageable pageable) {

        Page<PaymentResponse> paymentPage = paymentService.findAllForAdmin(pageable);
        PageResponse<PaymentResponse> pageResponse = PageResponse.fromPage(paymentPage);

        return ResponseEntity.ok(
                ResponseWrapper.<PageResponse<PaymentResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(pageResponse)
                        .build()
        );
    }
}