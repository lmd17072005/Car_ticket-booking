package com.ra.base_spring_boot.dto.seat;

import com.ra.base_spring_boot.model.Bus.Seat;
import com.ra.base_spring_boot.model.constants.SeatType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SeatResponse {
    private Long id;
    private Long busId;
    private String seatNumber;
    private SeatType seatType;
    private BigDecimal priceForSeatType;

    public SeatResponse(Seat seat) {
        this.id = seat.getId();
        this.busId = seat.getBus().getId();
        this.seatNumber = seat.getSeatNumber();
        this.seatType = seat.getSeatType();
        this.priceForSeatType = seat.getPriceForSeatType();
    }
}