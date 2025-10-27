package com.ra.base_spring_boot.controller;


import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.payment.CancellationPolicyRequest;
import com.ra.base_spring_boot.dto.payment.CancellationPolicyResponse;
import com.ra.base_spring_boot.services.payment.ICancellationPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cancellation-policies")
@RequiredArgsConstructor
public class CancellationPolicyController {

    private final ICancellationPolicyService cancellationPolicyService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<CancellationPolicyResponse>>> getAll() {
        return ResponseEntity.ok(ResponseWrapper.<List<CancellationPolicyResponse>>builder()
                .status(HttpStatus.OK).data(cancellationPolicyService.findAll()).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<CancellationPolicyResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseWrapper.<CancellationPolicyResponse>builder()
                .status(HttpStatus.OK).data(cancellationPolicyService.findById(id)).build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<CancellationPolicyResponse>> create(@Valid @RequestBody CancellationPolicyRequest request) {
        return new ResponseEntity<>(ResponseWrapper.<CancellationPolicyResponse>builder().status(HttpStatus.CREATED).data(cancellationPolicyService.save(request)).build(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<CancellationPolicyResponse>> update(@PathVariable Long id, @Valid @RequestBody CancellationPolicyRequest request) {
        return ResponseEntity.ok(ResponseWrapper.<CancellationPolicyResponse>builder()
                .status(HttpStatus.OK).data(cancellationPolicyService.update(id, request)).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<String>> delete(@PathVariable Long id) {
        cancellationPolicyService.delete(id);
        return ResponseEntity.ok(ResponseWrapper.<String>builder().status(HttpStatus.OK).data("Cancellation Policy deleted successfully").build());
    }
}
