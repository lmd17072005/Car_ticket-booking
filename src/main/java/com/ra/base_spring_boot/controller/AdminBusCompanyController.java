package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.bus.BusCompanyRequest;
import com.ra.base_spring_boot.dto.bus.BusCompanyResponse;
import com.ra.base_spring_boot.services.bus.IBusCompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/bus-companies")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminBusCompanyController {

    private final IBusCompanyService busCompanyService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<BusCompanyResponse>>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(
                ResponseWrapper.<Page<BusCompanyResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(busCompanyService.findAll(pageable, search))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<BusCompanyResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ResponseWrapper.<BusCompanyResponse>builder()
                        .status(HttpStatus.OK)
                        .data(busCompanyService.findById(id))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<BusCompanyResponse>> create(@Valid @RequestBody BusCompanyRequest request) {
        return new ResponseEntity<>(
                ResponseWrapper.<BusCompanyResponse>builder()
                        .status(HttpStatus.CREATED)
                        .data(busCompanyService.save(request))
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<BusCompanyResponse>> update(@PathVariable Long id, @Valid @RequestBody BusCompanyRequest request) {
        return ResponseEntity.ok(
                ResponseWrapper.<BusCompanyResponse>builder()
                        .status(HttpStatus.OK)
                        .data(busCompanyService.update(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> delete(@PathVariable Long id) {
        busCompanyService.delete(id);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .data("Company deleted successfully")
                        .build()
        );
    }
}