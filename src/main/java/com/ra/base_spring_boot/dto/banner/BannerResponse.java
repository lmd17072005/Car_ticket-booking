package com.ra.base_spring_boot.dto.banner;

import com.ra.base_spring_boot.model.others.Banner;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerResponse {
    private Long id;
    private String bannerUrl;
    private String position;

    public BannerResponse(Banner banner) {
        this.id = banner.getId();
        this.bannerUrl = banner.getBannerUrl();
        this.position = banner.getPosition();
    }
}
