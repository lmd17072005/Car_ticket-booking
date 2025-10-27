package com.ra.base_spring_boot.dto.ticket;

import com.ra.base_spring_boot.model.Bus.Ticket;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TicketResponse {
    private Long id;
    private String passengerName;
    private String routeDescription;
    private String licensePlate;
    private String seatNumber;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;
    private String status;

    public TicketResponse(Ticket ticket) {
        this.id = ticket.getId();
        this.departureTime = ticket.getDepartureTime();
        this.arrivalTime = ticket.getArrivalTime();
        this.price = ticket.getPrice();
        this.status = ticket.getStatus().name();
        if (ticket.getUser() != null) {
            this.passengerName = ticket.getUser().getFirstName() + " " + ticket.getUser().getLastName();
        }

        if (ticket.getSeat() != null) {
            this.seatNumber = ticket.getSeat().getSeatNumber();
        }

        if (ticket.getSchedule() != null) {
            this.licensePlate = ticket.getSchedule().getBus().getLicensePlate();
            this.routeDescription = ticket.getSchedule().getRoute().getDepartureStation().getName() + " -> " + ticket.getSchedule().getRoute().getArrivalStation().getName();
        }

    }

}
