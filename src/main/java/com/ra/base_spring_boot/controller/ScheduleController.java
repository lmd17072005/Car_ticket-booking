package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.schedule.ScheduleRequest;
import com.ra.base_spring_boot.dto.schedule.ScheduleResponse;
import com.ra.base_spring_boot.services.schedule.IScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/schedules")
@RequiredArgsConstructor

public class ScheduleController {
    private final IScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<ScheduleResponse>>> getAllSchedules() {
        return ResponseEntity.ok(ResponseWrapper.
                <List<ScheduleResponse>>builder().status(HttpStatus.OK).data(scheduleService.findAll()).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ScheduleResponse>> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseWrapper.
                <ScheduleResponse>builder().status(HttpStatus.OK).data(scheduleService.findById(id)).build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<ScheduleResponse>> createSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest) {
        return new ResponseEntity<>(ResponseWrapper.
                <ScheduleResponse>builder().status(HttpStatus.CREATED).data(scheduleService.save(scheduleRequest)).build(), HttpStatus.CREATED);
    }
}
