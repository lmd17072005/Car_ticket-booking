package com.ra.base_spring_boot.services.seat;

import com.ra.base_spring_boot.dto.seat.ScheduleSeatResponse;
import com.ra.base_spring_boot.dto.seat.SeatRequest;
import com.ra.base_spring_boot.dto.seat.SeatResponse;
import java.util.List;

public interface ISeatService {

    List<SeatResponse> getSeatsByBusId(Long busId);
    SeatResponse addSeat(SeatRequest seatRequest);
    void deleteSeat(Long seatId);

    List<ScheduleSeatResponse> getSeatStatusForSchedule(Long scheduleId);
}