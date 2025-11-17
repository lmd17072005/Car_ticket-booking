package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.destination.DestinationResponse;
import com.ra.base_spring_boot.services.destination.IDestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/destinations")
@RequiredArgsConstructor
public class DestinationController {

    private final IDestinationService destinationService;

    @GetMapping("/featured")
    public ResponseEntity<ResponseWrapper<List<DestinationResponse>>> getFeaturedDestinations() {
        return ResponseEntity.ok(
                ResponseWrapper.<List<DestinationResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(destinationService.getFeaturedDestinations())
                        .build()
        );
    }
}