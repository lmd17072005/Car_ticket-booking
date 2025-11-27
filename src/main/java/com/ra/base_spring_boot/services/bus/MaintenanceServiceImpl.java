package com.ra.base_spring_boot.services.bus;

import com.ra.base_spring_boot.dto.maintenance.MaintenanceRequest;
import com.ra.base_spring_boot.dto.maintenance.MaintenanceResponse;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Bus;
import com.ra.base_spring_boot.model.Bus.BusMaintenance;
import com.ra.base_spring_boot.model.constants.BusStatus;
import com.ra.base_spring_boot.model.constants.MaintenanceStatus;
import com.ra.base_spring_boot.repository.bus.IBusMaintenanceRepository;
import com.ra.base_spring_boot.repository.bus.IBusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements IMaintenanceService {

    private final IBusMaintenanceRepository maintenanceRepository;
    private final IBusRepository busRepository;

    @Override
    public List<MaintenanceResponse> findAll() {
        return maintenanceRepository.findAllByOrderByStartDateDesc().stream()
                .map(MaintenanceResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceResponse> getByBusId(Long busId) {
        if (!busRepository.existsById(busId)) {
            throw new HttpNotFound("Không tìm thấy xe với ID: " + busId);
        }
        return maintenanceRepository.findByBusIdOrderByStartDateDesc(busId).stream()
                .map(MaintenanceResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceResponse findById(Long id) {
        BusMaintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy lịch bảo trì với ID: " + id));
        return new MaintenanceResponse(maintenance);
    }

    @Override
    @Transactional
    public MaintenanceResponse save(MaintenanceRequest request) {
        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy xe bus với ID: " + request.getBusId()));

        BusMaintenance maintenance = new BusMaintenance();
        mapRequestToEntity(maintenance, request, bus);

        if (maintenance.getStatus() == null) {
            maintenance.setStatus(MaintenanceStatus.PLANNED);
        }

        updateBusStatusAutomatically(bus, maintenance.getStatus());

        return new MaintenanceResponse(maintenanceRepository.save(maintenance));
    }

    @Override
    @Transactional
    public MaintenanceResponse update(Long id, MaintenanceRequest request) {
        BusMaintenance existingMaintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy lịch bảo trì với ID: " + id));

        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy xe bus với ID: " + request.getBusId()));

        mapRequestToEntity(existingMaintenance, request, bus);

        updateBusStatusAutomatically(bus, existingMaintenance.getStatus());

        return new MaintenanceResponse(maintenanceRepository.save(existingMaintenance));
    }

    @Override
    public void delete(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new HttpNotFound("Không tìm thấy lịch bảo trì với ID: " + id);
        }
        maintenanceRepository.deleteById(id);
    }

    private void mapRequestToEntity(BusMaintenance maintenance, MaintenanceRequest request, Bus bus) {
        maintenance.setBus(bus);
        maintenance.setTitle(request.getTitle());
        maintenance.setStartDate(request.getStartDate());
        maintenance.setEstimatedCost(request.getEstimatedCost());

        if (request.getType() != null) {
            maintenance.setType(request.getType());
        }
        if (request.getStatus() != null) {
            maintenance.setStatus(request.getStatus());
        }
    }

    private void updateBusStatusAutomatically(Bus bus, MaintenanceStatus maintenanceStatus) {
        if (maintenanceStatus == MaintenanceStatus.IN_PROGRESS) {
            bus.setStatus(BusStatus.MAINTENANCE);
            busRepository.save(bus);
        } else if (maintenanceStatus == MaintenanceStatus.DONE) {
            if (bus.getStatus() == BusStatus.MAINTENANCE) {
                bus.setStatus(BusStatus.ACTIVE);
                busRepository.save(bus);
            }
        }
    }
}