package com.ra.base_spring_boot.dto.schedule;

import com.ra.base_spring_boot.model.Bus.Schedule;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class ScheduleResponse {
    private Long id;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private long durationMinutes;
    private int availableSeats;
    private String status;

    private String companyName;
    private String busName;
    private String busType;
    private String licensePlate;


    private String departureStationName;
    private String arrivalStationName;
    private BigDecimal price;


    private double averageRating;
    private int totalRatings;

    public ScheduleResponse(Schedule schedule) {
        this.id = schedule.getId();
        this.departureTime = schedule.getDepartureTime();
        this.arrivalTime = schedule.getArrivalTime();
        this.durationMinutes = ChronoUnit.MINUTES.between(schedule.getDepartureTime(), schedule.getArrivalTime());
        this.availableSeats = schedule.getAvailableSeats();
        this.status = schedule.getStatus().name();

        if (schedule.getBus() != null) {
            this.busName = schedule.getBus().getName();
            this.busType = schedule.getBus().getDescriptions(); // Dùng descriptions làm loại xe
            this.licensePlate = schedule.getBus().getLicensePlate();
            if (schedule.getBus().getCompany() != null) {
                this.companyName = schedule.getBus().getCompany().getCompanyName();
            }
        }

        if (schedule.getRoute() != null) {
            this.price = schedule.getRoute().getPrice();
            if (schedule.getRoute().getDepartureStation() != null) {
                this.departureStationName = schedule.getRoute().getDepartureStation().getName();
            }
            if (schedule.getRoute().getArrivalStation() != null) {
                this.arrivalStationName = schedule.getRoute().getArrivalStation().getName();
            }
        }
        this.averageRating = 0;
        this.totalRatings = 0;
    }
}