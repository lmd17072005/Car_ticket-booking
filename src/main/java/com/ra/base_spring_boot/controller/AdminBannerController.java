package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.banner.BannerRequest;
import com.ra.base_spring_boot.dto.banner.BannerResponse;
import com.ra.base_spring_boot.services.banner.IBannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/banners")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminBannerController {
    private final IBannerService bannerService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<BannerResponse>>> getAllBannersForAdmin(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(
                ResponseWrapper.<Page<BannerResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(bannerService.findAllForAdmin(pageable, search))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<BannerResponse>> getBannerById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ResponseWrapper.<BannerResponse>builder()
                        .status(HttpStatus.OK)
                        .data(bannerService.findById(id))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<BannerResponse>> createBanner(@Valid @RequestBody BannerRequest request) {
        return new ResponseEntity<>(
                ResponseWrapper.<BannerResponse>builder()
                        .status(HttpStatus.CREATED)
                        .data(bannerService.save(request))
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<BannerResponse>> updateBanner(@PathVariable Long id, @Valid @RequestBody BannerRequest request) {
        return ResponseEntity.ok(
                ResponseWrapper.<BannerResponse>builder()
                        .status(HttpStatus.OK)
                        .data(bannerService.update(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteBanner(@PathVariable Long id) {
        bannerService.delete(id);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .data("Delete banner successfully")
                        .build()
        );
    }

}
