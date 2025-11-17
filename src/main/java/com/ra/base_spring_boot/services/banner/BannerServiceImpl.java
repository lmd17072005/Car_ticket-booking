package com.ra.base_spring_boot.services.banner;

import com.ra.base_spring_boot.dto.banner.BannerRequest;
import com.ra.base_spring_boot.dto.banner.BannerResponse;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.others.Banner;
import com.ra.base_spring_boot.repository.banner.IBannerRepository;
import com.ra.base_spring_boot.services.file.IFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements IBannerService {

    private final IBannerRepository bannerRepository;
    private final IFileStorageService fileStorageService;

    @Override
    public List<BannerResponse> getPublicBanners() {
        return bannerRepository.findAll().stream()
                .map(BannerResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BannerResponse> findAllForAdmin(Pageable pageable, String search) {
        Page<Banner> bannerPage;
        if (search != null && !search.isEmpty()) {
            bannerPage = bannerRepository.findByPositionContainingIgnoreCase(search, pageable);
        } else {
            bannerPage = bannerRepository.findAll(pageable);
        }
        return bannerPage.map(BannerResponse::new);
    }

    @Override
    public BannerResponse findById(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Banner not found with id: " + id));
        return new BannerResponse(banner);
    }

    @Override
    public BannerResponse save(BannerRequest request) {
        Banner newBanner = new Banner();
        newBanner.setBannerUrl(request.getBannerUrl());
        newBanner.setPosition(request.getPosition());
        return new BannerResponse(bannerRepository.save(newBanner));
    }

    @Override
    public BannerResponse update(Long id, BannerRequest request) {
        Banner existingBanner = bannerRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Banner not found with id: " + id));

        existingBanner.setBannerUrl(request.getBannerUrl());
        existingBanner.setPosition(request.getPosition());

        return new BannerResponse(bannerRepository.save(existingBanner));
    }

    @Override
    public void delete(Long id) {
        if (!bannerRepository.existsById(id)) {
            throw new HttpNotFound("Banner not found with id: " + id);
        }
        bannerRepository.deleteById(id);
    }

    @Override
    public BannerResponse uploadImage(Long bannerId, MultipartFile imageFile) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy banner với ID: " + bannerId));

        String imageUrl = fileStorageService.uploadFile(imageFile);
        banner.setBannerUrl(imageUrl);

        Banner savedBanner = bannerRepository.save(banner);
        return new BannerResponse(savedBanner);
    }
}