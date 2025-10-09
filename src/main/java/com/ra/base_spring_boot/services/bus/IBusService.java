package com.ra.base_spring_boot.services.bus;

import com.ra.base_spring_boot.dto.bus.BusRequest;
import com.ra.base_spring_boot.dto.bus.BusResponse;
import java.util.List;

public interface IBusService {
    List<BusResponse> findAll();
    BusResponse findById(Long id);
    BusResponse save(BusRequest busRequest);
    BusResponse update(Long id, BusRequest busRequest);
    void delete(Long id);
}
