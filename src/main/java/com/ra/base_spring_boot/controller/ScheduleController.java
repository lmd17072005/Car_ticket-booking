package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.schedule.ScheduleRequest;
import com.ra.base_spring_boot.dto.schedule.ScheduleResponse;
import com.ra.base_spring_boot.dto.seat.ScheduleSeatResponse;
import com.ra.base_spring_boot.model.constants.SeatType;
import com.ra.base_spring_boot.services.schedule.IScheduleService;
import com.ra.base_spring_boot.services.seat.ISeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final IScheduleService scheduleService;
    private final ISeatService seatService;


    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ScheduleResponse>> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseWrapper.
                <ScheduleResponse>builder().status(HttpStatus.OK).data(scheduleService.findById(id)).build());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseWrapper<Page<ScheduleResponse>>> searchSchedules(
            @RequestParam Long departureStationId,
            @RequestParam Long arrivalStationId,
            @RequestParam String departureDate,
            @RequestParam(required = false) Integer fromHour,
            @RequestParam(required = false) Integer toHour,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<Long> companyIds,
            @RequestParam(required = false) List<SeatType> seatTypes,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        List<SeatType> finalSeatTypes = (seatTypes != null && seatTypes.isEmpty()) ? null : seatTypes;

        return ResponseEntity.ok(
                ResponseWrapper.<Page<ScheduleResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(scheduleService.searchSchedules(
                                departureStationId, arrivalStationId, departureDate,
                                fromHour, toHour, maxPrice, companyIds,finalSeatTypes,
                                pageable
                        ))
                        .build()
        );
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<ResponseWrapper<List<ScheduleSeatResponse>>> getScheduleSeats(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseWrapper.<List<ScheduleSeatResponse>>builder()
                .status(HttpStatus.OK)
                .data(seatService.getSeatStatusForSchedule(id))
                .build());
    }
}