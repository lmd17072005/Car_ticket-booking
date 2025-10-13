package com.ra.base_spring_boot.dto.route;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RouteRequest {
    @NotNull(message = "Depature station ID cannot be null")
    private Long departureStationId;

    @NotNull(message = "Arrival station ID cannot be null")
    private Long arrivalStationId;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be more than or equal to 0")
    private BigDecimal price;

    private Integer duration; // in minutes
    private Integer distance; // in kilometers
}
