package com.ra.base_spring_boot.services.bus;

import com.ra.base_spring_boot.dto.bus.BusAdminResponse;
import com.ra.base_spring_boot.dto.bus.BusRequest;
import com.ra.base_spring_boot.dto.bus.BusResponse;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Bus;
import com.ra.base_spring_boot.model.Bus.BusCompany;
import com.ra.base_spring_boot.model.constants.BusStatus;
import com.ra.base_spring_boot.model.constants.BusType;
import com.ra.base_spring_boot.repository.bus.IBusCompanyRepository;
import com.ra.base_spring_boot.repository.bus.IBusRepository;
import com.ra.base_spring_boot.specification.BusSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusServiceImpl implements IBusService {

    private final IBusRepository busRepository;
    private final IBusCompanyRepository busCompanyRepository;

    @Override
    public Page<BusResponse> findAllPublic(Pageable pageable) {
        Specification<Bus> spec = (root, query, cb) -> cb.equal(root.get("status"), BusStatus.ACTIVE);

        return busRepository.findAll(spec, pageable).map(BusResponse::new);
    }

    @Override
    public Page<BusAdminResponse> findAllAdmin(String search, BusStatus status, BusType type, Pageable pageable) {
        Specification<Bus> spec = BusSpecification.filterBuses(search, status, type);
        return busRepository.findAll(spec, pageable).map(BusAdminResponse::new);
    }

    @Override
    public BusResponse findById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Not found bus with id: " + id));
        return new BusResponse(bus);
    }

    @Override
    public BusResponse save(BusRequest busRequest) {
        if (busRepository.existsByLicensePlate(busRequest.getLicensePlate())) {
            throw new HttpConflict("License plate " + busRequest.getLicensePlate() + " already exists");
        }

        Bus newBus = new Bus();
        mapRequestToEntity(newBus, busRequest);

        if (newBus.getStatus() == null) {
            newBus.setStatus(BusStatus.ACTIVE);
        }

        return new BusResponse(busRepository.save(newBus));
    }

    @Override
    public BusResponse update(Long id, BusRequest busRequest) {
        Bus existingBus = busRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Not found bus with id: " + id));

        if (busRepository.existsByLicensePlateAndIdNot(busRequest.getLicensePlate(), id)) {
            throw new HttpConflict("License plate " + busRequest.getLicensePlate() + " already exists");
        }

        mapRequestToEntity(existingBus, busRequest);

        return new BusResponse(busRepository.save(existingBus));
    }

    @Override
    public void delete(Long id) {
        if (!busRepository.existsById(id)) {
            throw new HttpNotFound("Not found bus with id: " + id);
        }
        busRepository.deleteById(id);
    }

    private void mapRequestToEntity(Bus bus, BusRequest request) {
        bus.setName(request.getName());
        bus.setDescriptions(request.getDescriptions());
        bus.setLicensePlate(request.getLicensePlate());
        bus.setCapacity(request.getCapacity());

        bus.setCurrentKilometers(request.getCurrentKilometers());
        bus.setLastMaintenanceDate(request.getLastMaintenanceDate());
        bus.setNextMaintenanceDate(request.getNextMaintenanceDate());

        if (request.getStatus() != null) {
            bus.setStatus(request.getStatus());
        }

        if (request.getBusType() != null) {
            bus.setBusType(request.getBusType());
        }

        if (request.getCompanyId() != null) {
            BusCompany busCompany = busCompanyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new HttpNotFound("Not found bus company with id: " + request.getCompanyId()));
            bus.setCompany(busCompany);
        }
    }
}