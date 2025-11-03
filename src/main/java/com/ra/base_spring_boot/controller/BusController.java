package com.ra.base_spring_boot.controller;


import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.bus.BusRequest;
import com.ra.base_spring_boot.dto.bus.BusResponse;
import com.ra.base_spring_boot.services.bus.IBusService;
import jakarta.validation.Valid;
import com.ra.base_spring_boot.dto.bus.BusImageResponse;
import com.ra.base_spring_boot.services.bus.IBusImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/buses")
@RequiredArgsConstructor
public class BusController {

    private final IBusService busService;
    private final IBusImageService busImageService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<BusResponse>>> getAllBuses() {
        return ResponseEntity.ok(
                ResponseWrapper.<List<BusResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(busService.findAll())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<BusResponse>> getBusById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ResponseWrapper.<BusResponse>builder()
                        .status(HttpStatus.OK)
                        .data(busService.findById(id))
                        .build()
        );
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<ResponseWrapper<List<BusImageResponse>>> getBusImages(@PathVariable("id") Long busId) {
        return ResponseEntity.ok(
                ResponseWrapper.<List<BusImageResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(busImageService.findImagesByBusId(busId))
                        .build()
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<BusResponse>> createBus(@Valid @RequestBody BusRequest busRequest) {
        return new ResponseEntity<>(
                ResponseWrapper.<BusResponse>builder()
                        .status(HttpStatus.CREATED)
                        .data(busService.save(busRequest))
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<BusResponse>> updateBus(@PathVariable Long id, @Valid @RequestBody BusRequest busRequest) {
        return ResponseEntity.ok(
                ResponseWrapper.<BusResponse>builder()
                        .status(HttpStatus.OK)
                        .data(busService.update(id, busRequest))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<String>> deleteBus(@PathVariable Long id) {
        busService.delete(id);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .data("Xóa xe bus thành công")
                        .build()
        );
    }

}
