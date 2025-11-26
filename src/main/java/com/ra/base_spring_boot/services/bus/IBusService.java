package com.ra.base_spring_boot.services.bus;

import com.ra.base_spring_boot.dto.bus.BusAdminResponse;
import com.ra.base_spring_boot.dto.bus.BusRequest;
import com.ra.base_spring_boot.dto.bus.BusResponse;
import com.ra.base_spring_boot.model.constants.BusStatus;
import com.ra.base_spring_boot.model.constants.BusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBusService {
    Page<BusResponse> findAllPublic(Pageable pageable);
    Page<BusAdminResponse> findAllAdmin(String search, BusStatus status, BusType type, Pageable pageable);
    BusResponse findById(Long id);
    BusResponse save(BusRequest busRequest);
    BusResponse update(Long id, BusRequest busRequest);
    void delete(Long id);
}
