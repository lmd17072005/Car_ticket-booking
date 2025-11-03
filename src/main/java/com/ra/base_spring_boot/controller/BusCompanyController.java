package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.bus.BusCompanyResponse;
import com.ra.base_spring_boot.services.bus.IBusCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bus-companies")
@RequiredArgsConstructor
public class BusCompanyController {

    private final IBusCompanyService busCompanyService;
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<BusCompanyResponse>>> getPublicBusCompanies() {
        return ResponseEntity.ok(
                ResponseWrapper.<List<BusCompanyResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(busCompanyService.getPublicBusCompanies())
                        .build()
        );
    }
}