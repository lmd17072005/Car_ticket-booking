package com.ra.base_spring_boot.services.bus;

import com.ra.base_spring_boot.dto.maintenance.MaintenanceRequest;
import com.ra.base_spring_boot.dto.maintenance.MaintenanceResponse;

import java.util.List;

public interface IMaintenanceService {
    List<MaintenanceResponse> findAll();

    List<MaintenanceResponse> getByBusId(Long busId);


    MaintenanceResponse findById(Long id);

    MaintenanceResponse save(MaintenanceRequest request);

    MaintenanceResponse update(Long id, MaintenanceRequest request);

    void delete(Long id);
}