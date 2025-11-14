package com.ra.base_spring_boot.services.station;

import com.ra.base_spring_boot.dto.station.StationRequest;
import com.ra.base_spring_boot.dto.station.StationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IStationService {
    List<StationResponse> getPublicStations();
    List<StationResponse> findPopular();
    List<StationResponse> findTopDestinations();

    Page<StationResponse> findAll(Pageable pageable, String search);
    StationResponse findById(Long id);
    StationResponse save(StationRequest request);
    StationResponse update(Long id, StationRequest request);
    void delete(Long id);
    StationResponse uploadImage(Long stationId, MultipartFile imageFile);
    StationResponse uploadWallpaper(Long stationId, MultipartFile wallpaperFile);
}