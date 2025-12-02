package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.schedule.ScheduleRequest;
import com.ra.base_spring_boot.dto.schedule.ScheduleResponse;
import com.ra.base_spring_boot.model.constants.ScheduleStatus;
import com.ra.base_spring_boot.services.schedule.IScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/schedules")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminScheduleController {

    private final IScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<ScheduleResponse>>> getSchedulesForAdmin(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) ScheduleStatus status,
            @RequestParam(name = "station", required = false) Long stationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "departureTime") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        LocalDate filterDate = date;

        if (status == ScheduleStatus.RUNNING || status == ScheduleStatus.COMPLETED) {
            filterDate = null;
        } else if (date == null) {
            filterDate = LocalDate.now();
        }

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ScheduleResponse> schedulePage = scheduleService.findAllForAdmin(filterDate, status, stationId, pageable);

        return ResponseEntity.ok(ResponseWrapper.<Page<ScheduleResponse>>builder()
                .status(HttpStatus.OK)
                .data(schedulePage)
                .build());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ScheduleResponse>> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseWrapper.<ScheduleResponse>builder()
                .status(HttpStatus.OK)
                .data(scheduleService.findById(id))
                .build());
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<ScheduleResponse>> createSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest) {
        return new ResponseEntity<>(ResponseWrapper.<ScheduleResponse>builder()
                .status(HttpStatus.CREATED)
                .data(scheduleService.save(scheduleRequest))
                .build(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ScheduleResponse>> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequest scheduleRequest) {
        return ResponseEntity.ok(ResponseWrapper.<ScheduleResponse>builder()
                .status(HttpStatus.OK)
                .data(scheduleService.update(id, scheduleRequest))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> cancelSchedule(@PathVariable Long id) {
        scheduleService.cancelSchedule(id);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .data("Hủy lịch trình thành công.")
                .build());
    }
}