package com.ra.base_spring_boot.model.Bus;

import com.ra.base_spring_boot.model.base.BaseObject;
import com.ra.base_spring_boot.model.constants.MaintenanceStatus;
import com.ra.base_spring_boot.model.constants.MaintenanceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bus_maintenances")
@Getter
@Setter
public class BusMaintenance extends BaseObject {

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type")
    private MaintenanceType type;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "estimated_cost")
    private BigDecimal estimatedCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;
}