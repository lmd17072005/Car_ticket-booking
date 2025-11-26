package com.ra.base_spring_boot.dto.bus;

import lombok.Getter;
import lombok.Setter;
import com.ra.base_spring_boot.model.Bus.Bus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class BusResponse {
    private Long id;
    private String name;
    private String descriptions;
    private String licensePlate;
    private int capacity;
    private Long companyId;
    private String companyName;
    private String busType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BusResponse(Bus bus) {
        this.id = bus.getId();
        this.name = bus.getName();
        this.descriptions = bus.getDescriptions();
        this.licensePlate = bus.getLicensePlate();
        this.capacity = bus.getCapacity();
        if (bus.getCompany() != null) {
            this.companyId = bus.getCompany().getId();
            this.companyName = bus.getCompany().getCompanyName();
        }
        if (bus.getBusType() != null) {
            this.busType = bus.getBusType().name();
        }
        this.createdAt = bus.getCreatedAt();
        this.updatedAt = bus.getUpdatedAt();
    }
}
