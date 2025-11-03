package com.ra.base_spring_boot.services.station;

import com.ra.base_spring_boot.dto.station.StationRequest;
import com.ra.base_spring_boot.dto.station.StationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IStationService {
    List<StationResponse> getPublicStations();

    Page<StationResponse> findAll(Pageable pageable, String search);
    StationResponse findById(Long id);
    StationResponse save(StationRequest request);
    StationResponse update(Long id, StationRequest request);
    void delete(Long id);
}