package com.ra.base_spring_boot.services.bus;

import com.ra.base_spring_boot.dto.bus.BusImageResponse;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Bus;
import com.ra.base_spring_boot.model.Bus.BusImage;
import com.ra.base_spring_boot.repository.bus.IBusImageRepository;
import com.ra.base_spring_boot.repository.bus.IBusRepository;
import com.ra.base_spring_boot.services.file.IFileStorageService;
import com.ra.base_spring_boot.services.bus.IBusImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusImageServiceImpl implements IBusImageService {
    private final IBusImageRepository busImageRepository;
    private final IBusRepository busRepository;
    private final IFileStorageService fileStorageService;

    @Override
    public BusImageResponse addImageToBus(Long busId, MultipartFile imageFile) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy xe bus với ID: " + busId));

        String imageUrl = fileStorageService.uploadFile(imageFile);

        BusImage busImage = new BusImage();
        busImage.setBus(bus);
        busImage.setImageUrl(imageUrl);

        return new BusImageResponse(busImageRepository.save(busImage));
    }

    @Override
    public void deleteImage(Long imageId) {
        if (!busImageRepository.existsById(imageId)) {
            throw new HttpNotFound("Không tìm thấy ảnh với ID: " + imageId);
        }
        busImageRepository.deleteById(imageId);
    }

    @Override
    public List<BusImageResponse> findImagesByBusId(Long busId) {
        if (!busRepository.existsById(busId)) {
            throw new HttpNotFound("Không tìm thấy xe bus với ID: " + busId);
        }
        return busRepository.findById(busId).get().getImages().stream()
                .map(BusImageResponse::new)
                .collect(Collectors.toList());
    }
}
