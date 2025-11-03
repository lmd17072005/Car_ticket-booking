package com.ra.base_spring_boot.dto.seat;

import com.ra.base_spring_boot.model.Bus.Seat;
import com.ra.base_spring_boot.model.constants.SeatStatus;
import com.ra.base_spring_boot.model.constants.SeatType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ScheduleSeatResponse {
    private Long id;
    private String seatNumber;
    private SeatType seatType;
    private BigDecimal priceForSeatType;
    private SeatStatus status;

    public ScheduleSeatResponse(Seat seat, boolean isBooked) {
        this.id = seat.getId();
        this.seatNumber = seat.getSeatNumber();
        this.seatType = seat.getSeatType();
        this.priceForSeatType = seat.getPriceForSeatType();
        this.status = isBooked ? SeatStatus.BOOKED : SeatStatus.AVAILABLE;
    }
}