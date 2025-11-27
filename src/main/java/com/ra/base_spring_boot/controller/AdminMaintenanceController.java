package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.maintenance.MaintenanceRequest;
import com.ra.base_spring_boot.dto.maintenance.MaintenanceResponse;
import com.ra.base_spring_boot.services.bus.IMaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/maintenances")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminMaintenanceController {

    private final IMaintenanceService maintenanceService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<MaintenanceResponse>>> getAll() {
        return ResponseEntity.ok(ResponseWrapper.<List<MaintenanceResponse>>builder()
                .status(HttpStatus.OK)
                .data(maintenanceService.findAll())
                .build());
    }


    @GetMapping("/bus/{busId}")
    public ResponseEntity<ResponseWrapper<List<MaintenanceResponse>>> getByBusId(@PathVariable Long busId) {
        return ResponseEntity.ok(ResponseWrapper.<List<MaintenanceResponse>>builder()
                .status(HttpStatus.OK)
                .data(maintenanceService.getByBusId(busId))
                .build());
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<MaintenanceResponse>> create(@Valid @RequestBody MaintenanceRequest request) {
        return new ResponseEntity<>(ResponseWrapper.<MaintenanceResponse>builder()
                .status(HttpStatus.CREATED)
                .data(maintenanceService.save(request))
                .build(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<MaintenanceResponse>> update(@PathVariable Long id, @Valid @RequestBody MaintenanceRequest request) {
        return ResponseEntity.ok(ResponseWrapper.<MaintenanceResponse>builder()
                .status(HttpStatus.OK)
                .data(maintenanceService.update(id, request))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> delete(@PathVariable Long id) {
        maintenanceService.delete(id);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .data("Xóa lịch bảo trì thành công")
                .build());
    }
}