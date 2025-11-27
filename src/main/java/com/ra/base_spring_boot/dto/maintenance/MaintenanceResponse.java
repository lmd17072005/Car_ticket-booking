package com.ra.base_spring_boot.dto.maintenance;

import com.ra.base_spring_boot.model.Bus.BusMaintenance;
import com.ra.base_spring_boot.model.constants.MaintenanceStatus;
import com.ra.base_spring_boot.model.constants.MaintenanceType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MaintenanceResponse {
    private Long id;
    private String title;
    private MaintenanceStatus status;
    private MaintenanceType type;
    private LocalDate startDate;
    private BigDecimal estimatedCost;
    private Long busId;
    private String busLicensePlate;

    public MaintenanceResponse(BusMaintenance m) {
        this.id = m.getId();
        this.title = m.getTitle();
        this.status = m.getStatus();
        this.type = m.getType();
        this.startDate = m.getStartDate();
        this.estimatedCost = m.getEstimatedCost();
        if (m.getBus() != null) {
            this.busId = m.getBus().getId();
            this.busLicensePlate = m.getBus().getLicensePlate();
        }
    }
}