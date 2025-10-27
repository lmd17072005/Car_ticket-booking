package com.ra.base_spring_boot.services.banner;

import com.ra.base_spring_boot.dto.banner.BannerRequest;
import com.ra.base_spring_boot.dto.banner.BannerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBannerService {
    List<BannerResponse> getPublicBanners();

    Page<BannerResponse> findAllForAdmin(Pageable pageable, String search);

    BannerResponse findById(Long id);
    BannerResponse save(BannerRequest request);
    BannerResponse update(Long id, BannerRequest request);
    void delete(Long id);
}
