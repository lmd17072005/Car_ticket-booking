package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.banner.BannerRequest;
import com.ra.base_spring_boot.dto.banner.BannerResponse;
import com.ra.base_spring_boot.services.banner.IBannerService;import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor

public class BannerController {

    private final IBannerService bannerService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<BannerResponse>>> getPublicBanners() {
        return ResponseEntity.ok(
                ResponseWrapper.<List<BannerResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(bannerService.getPublicBanners())
                        .build()
        );
    }
}
