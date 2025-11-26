package com.ra.base_spring_boot.dto.bus;


import com.ra.base_spring_boot.model.Bus.Bus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class BusAdminResponse extends BusResponse {
    private String status;
    private Integer currentKilometers;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BusAdminResponse(Bus bus) {
        super(bus);

        if (bus.getStatus() != null) this.status = bus.getStatus().name();
        this.currentKilometers = bus.getCurrentKilometers();
        this.lastMaintenanceDate = bus.getLastMaintenanceDate();
        this.nextMaintenanceDate = bus.getNextMaintenanceDate();
        this.createdAt = bus.getCreatedAt();
        this.updatedAt = bus.getUpdatedAt();
    }
}