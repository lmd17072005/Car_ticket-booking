package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.station.StationResponse;
import com.ra.base_spring_boot.services.station.IStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationController {

    private final IStationService stationService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<StationResponse>>> getPublicStations() {
        return ResponseEntity.ok(
                ResponseWrapper.<List<StationResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(stationService.getPublicStations())
                        .build()
        );
    }

    @GetMapping("/popular")
    public ResponseEntity<ResponseWrapper<List<StationResponse>>> getPopularStations() {
        return ResponseEntity.ok(ResponseWrapper.<List<StationResponse>>builder()
                .status(HttpStatus.OK)
                .data(stationService.findPopular())
                .build());
    }

    @GetMapping("/top-destinations")
    public ResponseEntity<ResponseWrapper<List<StationResponse>>> getTopDestinations() {
        return ResponseEntity.ok(ResponseWrapper.<List<StationResponse>>builder()
                .status(HttpStatus.OK)
                .data(stationService.findTopDestinations())
                .build());
    }
}