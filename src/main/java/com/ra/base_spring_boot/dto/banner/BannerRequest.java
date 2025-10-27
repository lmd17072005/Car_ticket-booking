package com.ra.base_spring_boot.dto.banner;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerRequest {
    @NotBlank(message = "Banner URL is required")
    private String bannerUrl;

    private String position;
}
