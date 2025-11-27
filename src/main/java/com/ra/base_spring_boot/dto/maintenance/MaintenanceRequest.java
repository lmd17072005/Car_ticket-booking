package com.ra.base_spring_boot.dto.maintenance;

import com.ra.base_spring_boot.model.constants.MaintenanceStatus;
import com.ra.base_spring_boot.model.constants.MaintenanceType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MaintenanceRequest {
    @NotNull(message = "ID xe không được để trống")
    private Long busId;

    @NotNull(message = "Tiêu đề không được để trống")
    private String title;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    private MaintenanceStatus status;

    private MaintenanceType type;

    private BigDecimal estimatedCost;
}