package com.ra.base_spring_boot.dto.route;

import com.ra.base_spring_boot.model.Bus.Route;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RouteResponse {
    private Long id;
    private Long departureStationId;
    private String departureStationName;
    private Long arrivalStationId;
    private String arrivalStationName;
    private BigDecimal price;
    private Integer duration;
    private Integer distance;
    private Boolean isPopular;
    private String imageUrl;

    public RouteResponse(Route route) {
        this.id = route.getId();
        this.price = route.getPrice();
        this.duration = route.getDuration();
        this.distance = route.getDistance();
        this.imageUrl = route.getImageUrl();
        this.isPopular = route.getIsPopular();
        if (route.getDepartureStation() != null) {
            this.departureStationId = route.getDepartureStation().getId();
            this.departureStationName = route.getDepartureStation().getName();
        }

        if (route.getArrivalStation() != null) {
            this.arrivalStationId = route.getArrivalStation().getId();
            this.arrivalStationName = route.getArrivalStation().getName();
        }
    }
}
