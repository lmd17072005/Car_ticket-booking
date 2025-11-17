package com.ra.base_spring_boot.services.station;

import com.ra.base_spring_boot.dto.station.StationRequest;
import com.ra.base_spring_boot.dto.station.StationResponse;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Station;
import com.ra.base_spring_boot.repository.route.IStationRepository;
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
public class StationServiceImpl implements IStationService {
    private final IStationRepository stationRepository;
    private final IFileStorageService fileStorageService;

    @Override
    public List<StationResponse> getPublicStations() {
        return stationRepository.findAll().stream()
                .map(StationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<StationResponse> findAll(Pageable pageable, String search) {
        Page<Station> stationPage;
        if (search != null && !search.isEmpty()) {
            stationPage = stationRepository.searchByNameOrLocation(search, pageable);
        } else {
            stationPage = stationRepository.findAll(pageable);
        }
        return stationPage.map(StationResponse::fromEntity);
    }

    @Override
    public StationResponse findById(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy bến xe với ID: " + id));
        return StationResponse.fromEntity(station);
    }

    @Override
    public StationResponse save(StationRequest request) {
        if (stationRepository.existsByNameIgnoreCase(request.getName())) {
            throw new HttpConflict("Tên bến xe '" + request.getName() + "' đã tồn tại.");
        }
        Station newStation = mapRequestToEntity(new Station(), request);
        return StationResponse.fromEntity(stationRepository.save(newStation));
    }

    @Override
    public StationResponse update(Long id, StationRequest request) {
        Station existingStation = stationRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy bến xe với ID: " + id));

        mapRequestToEntity(existingStation, request);
        return StationResponse.fromEntity(stationRepository.save(existingStation));
    }

    @Override
    public void delete(Long id) {
        if (!stationRepository.existsById(id)) {
            throw new HttpNotFound("Không tìm thấy bến xe với ID: " + id);
        }

        stationRepository.deleteById(id);
    }

    @Override
    public List<StationResponse> findPopular() {
        return stationRepository.findByIsPopularTrue().stream()
                .map(StationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public StationResponse uploadImage(Long stationId, MultipartFile imageFile) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy bến xe với ID: " + stationId));

        String imageUrl = fileStorageService.uploadFile(imageFile);
        station.setImage(imageUrl);

        Station savedStation = stationRepository.save(station);
        return StationResponse.fromEntity(savedStation);
    }


    @Override
    public StationResponse uploadWallpaper(Long stationId, MultipartFile wallpaperFile) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy bến xe với ID: " + stationId));

        String wallpaperUrl = fileStorageService.uploadFile(wallpaperFile);
        station.setWallpaper(wallpaperUrl);

        Station savedStation = stationRepository.save(station);
        return StationResponse.fromEntity(savedStation);
    }


    private Station mapRequestToEntity(Station station, StationRequest request) {
        station.setName(request.getName());
        station.setImage(request.getImage());
        station.setWallpaper(request.getWallpaper());
        station.setDescriptions(request.getDescriptions());
        station.setLocation(request.getLocation());
        return station;
    }
}