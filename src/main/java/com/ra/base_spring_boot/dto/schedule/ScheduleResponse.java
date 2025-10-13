package com.ra.base_spring_boot.dto.schedule;

import com.ra.base_spring_boot.model.Bus.Schedule;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleResponse {
    private Long id;
    private Long routeId;
    private String routeDescription;
    private Long busId;
    private String busLicensePlate;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int availableSeats;
    private String status;

    public ScheduleResponse(Schedule schedule) {
        this.id = schedule.getId();
        this.departureTime = schedule.getDepartureTime();
        this.arrivalTime = schedule.getArrivalTime();
        this.availableSeats = schedule.getAvailableSeats();
        this.status = schedule.getStatus().name();
        if (schedule.getRoute() != null) {
            this.routeId = schedule.getRoute().getId();
            this.routeDescription = schedule.getRoute().getDepartureStation().getName() + " to " +
                    schedule.getRoute().getArrivalStation().getName();
        }
        if (schedule.getBus() != null) {
            this.busId = schedule.getBus().getId();
            this.busLicensePlate = schedule.getBus().getLicensePlate();
        }
    }
}
