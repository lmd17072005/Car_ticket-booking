package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.station.StationRequest;
import com.ra.base_spring_boot.dto.station.StationResponse;
import com.ra.base_spring_boot.services.station.IStationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/stations")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminStationController {

    private final IStationService stationService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<StationResponse>>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(
                ResponseWrapper.<Page<StationResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(stationService.findAll(pageable, search))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<StationResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ResponseWrapper.<StationResponse>builder()
                        .status(HttpStatus.OK)
                        .data(stationService.findById(id))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<StationResponse>> create(@Valid @RequestBody StationRequest request) {
        return new ResponseEntity<>(
                ResponseWrapper.<StationResponse>builder()
                        .status(HttpStatus.CREATED)
                        .data(stationService.save(request))
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<StationResponse>> update(@PathVariable Long id, @Valid @RequestBody StationRequest request) {
        return ResponseEntity.ok(
                ResponseWrapper.<StationResponse>builder()
                        .status(HttpStatus.OK)
                        .data(stationService.update(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> delete(@PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .data("Xóa bến xe thành công")
                        .build()
        );
    }

    @PostMapping(value = "/{id}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<StationResponse>> uploadImage(
            @PathVariable("id") Long stationId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                ResponseWrapper.<StationResponse>builder()
                        .status(HttpStatus.OK)
                        .data(stationService.uploadImage(stationId, file))
                        .build()
        );
    }

    @PostMapping(value = "/{id}/upload-wallpaper", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<StationResponse>> uploadWallpaper(
            @PathVariable("id") Long stationId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                ResponseWrapper.<StationResponse>builder()
                        .status(HttpStatus.OK)
                        .data(stationService.uploadWallpaper(stationId, file))
                        .build()
        );
    }
}