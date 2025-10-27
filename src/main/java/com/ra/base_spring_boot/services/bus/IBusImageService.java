package com.ra.base_spring_boot.services.bus;

import com.ra.base_spring_boot.dto.bus.BusImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBusImageService {
    BusImageResponse addImageToBus(Long busId, MultipartFile imageFile);
    void deleteImage(Long imageId);
    List<BusImageResponse> findImagesByBusId(Long busId);
}
