package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.seat.SeatRequest;
import com.ra.base_spring_boot.dto.seat.SeatResponse;
import com.ra.base_spring_boot.services.seat.ISeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/seats")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminSeatController {

    private final ISeatService seatService;

    @GetMapping("/bus/{busId}")
    public ResponseEntity<ResponseWrapper<List<SeatResponse>>> getSeatsByBus(@PathVariable Long busId) {
        return ResponseEntity.ok(ResponseWrapper.<List<SeatResponse>>builder()
                .status(HttpStatus.OK)
                .data(seatService.getSeatsByBusId(busId))
                .build());
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<SeatResponse>> createSeat(@Valid @RequestBody SeatRequest seatRequest) {
        return new ResponseEntity<>(ResponseWrapper.<SeatResponse>builder()
                .status(HttpStatus.CREATED)
                .data(seatService.addSeat(seatRequest))
                .build(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{seatId}")
    public ResponseEntity<ResponseWrapper<String>> deleteSeat(@PathVariable Long seatId) {
        seatService.deleteSeat(seatId);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .data("Xóa ghế thành công")
                .build());
    }
}