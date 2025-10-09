package com.ra.base_spring_boot.services.bus;

import com.ra.base_spring_boot.dto.bus.BusRequest;
import com.ra.base_spring_boot.dto.bus.BusResponse;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Bus;
import com.ra.base_spring_boot.model.Bus.BusCompany;
import com.ra.base_spring_boot.repository.bus.IBusCompanyRepository;
import com.ra.base_spring_boot.repository.bus.IBusRepository;
import com.ra.base_spring_boot.services.bus.IBusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusServiceImpl implements IBusService {
    private final IBusRepository busRepository;
    private final IBusCompanyRepository busCompanyRepository;

    @Override
    public List<BusResponse> findAll() {
        return busRepository.findAll().stream()
                .map(BusResponse::new)
                .collect(Collectors.toList());
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

        BusCompany busCompany = busCompanyRepository.findById(busRequest.getCompanyId())
                .orElseThrow(() -> new HttpNotFound("Not found bus company with id: " + busRequest.getCompanyId()));

        Bus newBus = new Bus();
        newBus.setName(busRequest.getName());
        newBus.setDescriptions(busRequest.getDescriptions());
        newBus.setLicensePlate(busRequest.getLicensePlate());
        newBus.setCapacity(busRequest.getCapacity());
        newBus.setCompany(busCompany);

        Bus savedBus = busRepository.save(newBus);
        return new BusResponse(savedBus);
    }

    @Override
    public BusResponse update(Long id, BusRequest busRequest) {
        Bus existingBus = busRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Not found bus with id: " + id));

        if (busRepository.existsByLicensePlateAndIdNot(busRequest.getLicensePlate(), id)) {
            throw new HttpConflict("License plate " + busRequest.getLicensePlate() + " already exists");
        }

        BusCompany busCompany = busCompanyRepository.findById(busRequest.getCompanyId())
                .orElseThrow(() -> new HttpNotFound("Not found bus company with id: " + busRequest.getCompanyId()));

        existingBus.setName(busRequest.getName());
        existingBus.setDescriptions(busRequest.getDescriptions());
        existingBus.setLicensePlate(busRequest.getLicensePlate());
        existingBus.setCapacity(busRequest.getCapacity());
        existingBus.setCompany(busCompany);

        Bus updatedBus = busRepository.save(existingBus);
        return new BusResponse(updatedBus);
    }

    @Override
    public void delete(Long id) {
        if (!busRepository.existsById(id)) {
            throw new HttpNotFound("Not found bus with id: " + id);
        }
        busRepository.deleteById(id);
    }
}
