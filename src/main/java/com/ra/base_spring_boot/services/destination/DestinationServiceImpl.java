package com.ra.base_spring_boot.services.destination;

import com.ra.base_spring_boot.dto.destination.DestinationRequest;
import com.ra.base_spring_boot.dto.destination.DestinationResponse;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.others.Destination;
import com.ra.base_spring_boot.repository.destination.IDestinationRepository;
import com.ra.base_spring_boot.services.file.IFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DestinationServiceImpl implements IDestinationService {

    private final IDestinationRepository destinationRepository;
    private final IFileStorageService fileStorageService;

    @Override
    public List<DestinationResponse> getFeaturedDestinations() {
        return destinationRepository.findByIsFeaturedTrue().stream()
                .map(DestinationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<DestinationResponse> findAll(Pageable pageable, String search) {
        Page<Destination> destinationPage;
        if (search != null && !search.isEmpty()) {
            destinationPage = destinationRepository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                    search, search, pageable);
        } else {
            destinationPage = destinationRepository.findAll(pageable);
        }
        return destinationPage.map(DestinationResponse::fromEntity);
    }

    @Override
    public DestinationResponse findById(Long id) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy điểm đến với ID: " + id));
        return DestinationResponse.fromEntity(destination);
    }

    @Override
    public DestinationResponse save(DestinationRequest request) {
        if (destinationRepository.existsByNameIgnoreCase(request.getName())) {
            throw new HttpConflict("Điểm đến '" + request.getName() + "' đã tồn tại.");
        }
        Destination newDestination = mapRequestToEntity(new Destination(), request);
        return DestinationResponse.fromEntity(destinationRepository.save(newDestination));
    }

    @Override
    public DestinationResponse update(Long id, DestinationRequest request) {
        Destination existingDestination = destinationRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy điểm đến với ID: " + id));

        mapRequestToEntity(existingDestination, request);
        return DestinationResponse.fromEntity(destinationRepository.save(existingDestination));
    }

    @Override
    public void delete(Long id) {
        if (!destinationRepository.existsById(id)) {
            throw new HttpNotFound("Không tìm thấy điểm đến với ID: " + id);
        }
        destinationRepository.deleteById(id);
    }

    @Override
    public DestinationResponse uploadImage(Long destinationId, MultipartFile file) {
        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy điểm đến với ID: " + destinationId));

        String imageUrl = fileStorageService.uploadFile(file);
        destination.setImage(imageUrl);

        return DestinationResponse.fromEntity(destinationRepository.save(destination));
    }

    @Override
    public DestinationResponse uploadWallpaper(Long destinationId, MultipartFile file) {
        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy điểm đến với ID: " + destinationId));

        String wallpaperUrl = fileStorageService.uploadFile(file);
        destination.setWallpaper(wallpaperUrl);

        return DestinationResponse.fromEntity(destinationRepository.save(destination));
    }

    private Destination mapRequestToEntity(Destination destination, DestinationRequest request) {
        destination.setName(request.getName());
        destination.setLocation(request.getLocation());
        destination.setImage(request.getImage());
        destination.setWallpaper(request.getWallpaper());
        destination.setDescription(request.getDescription());

        if (request.getReviewCount() != null) {
            destination.setReviewCount(request.getReviewCount());
        }

        if (request.getIsFeatured() != null) {
            destination.setIsFeatured(request.getIsFeatured());
        } else {
            destination.setIsFeatured(false);
        }

        return destination;
    }
}