package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.provider.ProviderRequest;
import com.ra.base_spring_boot.dto.provider.ProviderResponse;
import com.ra.base_spring_boot.services.provider.IProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/payment-providers")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class ProviderController {
    private final IProviderService providerService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<ProviderResponse>>> findAll() {
        return ResponseEntity.ok(ResponseWrapper.<List<ProviderResponse>>builder()
                .status(HttpStatus.OK).data(providerService.findAll()).build());
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<ProviderResponse>> create(@Valid @RequestBody ProviderRequest request) {
        return new ResponseEntity<>(ResponseWrapper.<ProviderResponse>builder()
                .status(HttpStatus.CREATED).data(providerService.save(request)).build(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> delete(@PathVariable Long id) {
        providerService.delete(id);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .data("Xóa nhà cung cấp thanh toán thành công")
                        .build()
        );
    }
}