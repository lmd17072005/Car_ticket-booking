package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.PageResponse;
import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.bus.BusImageResponse;
import com.ra.base_spring_boot.dto.bus.BusRequest;
import com.ra.base_spring_boot.dto.bus.BusResponse;
import com.ra.base_spring_boot.model.constants.BusStatus;
import com.ra.base_spring_boot.services.bus.IBusImageService;
import com.ra.base_spring_boot.services.bus.IBusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buses")
@RequiredArgsConstructor
public class BusController {

    private final IBusService busService;
    private final IBusImageService busImageService;


    @GetMapping
    public ResponseEntity<ResponseWrapper<PageResponse<BusResponse>>> getAllBuses(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<BusResponse> busPage = busService.findAllPublic(pageable);
        return ResponseEntity.ok(
                ResponseWrapper.<PageResponse<BusResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(PageResponse.fromPage(busPage))
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
}