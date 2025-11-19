// File: src/main/java/com/ra/base_spring_boot/services/destination/IDestinationService.java

package com.ra.base_spring_boot.services.destination;

import com.ra.base_spring_boot.dto.destination.DestinationRequest;
import com.ra.base_spring_boot.dto.destination.DestinationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface IDestinationService {
    List<DestinationResponse> findAll();

    List<DestinationResponse> getFeaturedDestinations();

    Page<DestinationResponse> findAll(Pageable pageable, String search);
    DestinationResponse findById(Long id);
    DestinationResponse save(DestinationRequest request);
    DestinationResponse update(Long id, DestinationRequest request);
    void delete(Long id);

    DestinationResponse uploadImage(Long destinationId, MultipartFile file);
    DestinationResponse uploadWallpaper(Long destinationId, MultipartFile file);
}