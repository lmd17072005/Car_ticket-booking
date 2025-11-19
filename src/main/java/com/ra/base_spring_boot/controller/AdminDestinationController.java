package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.destination.DestinationRequest;
import com.ra.base_spring_boot.dto.destination.DestinationResponse;
import com.ra.base_spring_boot.services.destination.IDestinationService;
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
@RequestMapping("/api/v1/admin/destinations")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminDestinationController {

    private final IDestinationService destinationService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<DestinationResponse>>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(
                ResponseWrapper.<Page<DestinationResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(destinationService.findAll(pageable, search))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<DestinationResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ResponseWrapper.<DestinationResponse>builder()
                        .status(HttpStatus.OK)
                        .data(destinationService.findById(id))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<DestinationResponse>> create(
            @Valid @RequestBody DestinationRequest request) {
        return new ResponseEntity<>(
                ResponseWrapper.<DestinationResponse>builder()
                        .status(HttpStatus.CREATED)
                        .data(destinationService.save(request))
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<DestinationResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody DestinationRequest request) {
        return ResponseEntity.ok(
                ResponseWrapper.<DestinationResponse>builder()
                        .status(HttpStatus.OK)
                        .data(destinationService.update(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> delete(@PathVariable Long id) {
        destinationService.delete(id);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .data("Xóa điểm đến thành công")
                        .build()
        );
    }

    @PostMapping(value = "/{id}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<DestinationResponse>> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                ResponseWrapper.<DestinationResponse>builder()
                        .status(HttpStatus.OK)
                        .data(destinationService.uploadImage(id, file))
                        .build()
        );
    }

    @PostMapping(value = "/{id}/upload-wallpaper", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<DestinationResponse>> uploadWallpaper(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                ResponseWrapper.<DestinationResponse>builder()
                        .status(HttpStatus.OK)
                        .data(destinationService.uploadWallpaper(id, file))
                        .build()
        );
    }
}