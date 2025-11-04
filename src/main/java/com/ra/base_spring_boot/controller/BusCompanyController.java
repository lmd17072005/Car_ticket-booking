package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.bus.BusCompanyResponse;
import com.ra.base_spring_boot.dto.schedule.ScheduleResponse;
import com.ra.base_spring_boot.services.bus.IBusCompanyService;
import com.ra.base_spring_boot.services.schedule.IScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bus-companies")
@RequiredArgsConstructor
public class BusCompanyController {

    private final IBusCompanyService busCompanyService;
    private final IScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<BusCompanyResponse>>> getPublicBusCompanies() {
        return ResponseEntity.ok(
                ResponseWrapper.<List<BusCompanyResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(busCompanyService.getPublicBusCompanies())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<BusCompanyResponse>> findById(@PathVariable Long id){
        return ResponseEntity.ok(
                ResponseWrapper.<BusCompanyResponse>builder()
                        .status(HttpStatus.OK)
                        .data(busCompanyService.findById(id))
                        .build()
        );
    }

    @GetMapping("/{id}/schedules")
    public ResponseEntity<ResponseWrapper<Page<ScheduleResponse>>> getSchedulesByCompany(
            @PathVariable("id") Long companyId,
            @RequestParam(required = false) String departureDate,
            @PageableDefault(page = 0, size = 10, sort = "departureTime,asc") Pageable pageable) {

        return ResponseEntity.ok(
                ResponseWrapper.<Page<ScheduleResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(scheduleService.findSchedulesByCompany(companyId, departureDate, pageable))
                        .build()
        );
    }
}