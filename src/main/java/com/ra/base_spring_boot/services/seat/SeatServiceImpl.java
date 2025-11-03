package com.ra.base_spring_boot.services.seat;
import com.ra.base_spring_boot.dto.seat.ScheduleSeatResponse;
import com.ra.base_spring_boot.dto.seat.SeatRequest;
import com.ra.base_spring_boot.dto.seat.SeatResponse;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Bus;
import com.ra.base_spring_boot.model.Bus.Schedule;
import com.ra.base_spring_boot.model.Bus.Seat;
import com.ra.base_spring_boot.repository.bus.IBusRepository;
import com.ra.base_spring_boot.repository.bus.ISeatRepository;
import com.ra.base_spring_boot.repository.schedule.IScheduleRepository;
import com.ra.base_spring_boot.repository.ticket.ITicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements ISeatService {
    private final ISeatRepository seatRepository;
    private final IBusRepository busRepository;
    private final IScheduleRepository scheduleRepository;
    private final ITicketRepository ticketRepository;

    @Override
    public List<SeatResponse> getSeatsByBusId(Long busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new HttpNotFound("Bus not found with id: " + busId));
        return seatRepository.findByBus(bus).stream()
                .map(SeatResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public SeatResponse addSeat(SeatRequest seatRequest) {
        Bus bus = busRepository.findById(seatRequest.getBusId())
                .orElseThrow(() -> new HttpNotFound("Bus not found with id: " + seatRequest.getBusId()));

        boolean isExist = bus.getSeats().stream()
                .anyMatch(seat -> seat.getSeatNumber().equalsIgnoreCase(seatRequest.getSeatNumber()));
        if (isExist) {
            throw new HttpConflict("Seat Number " + seatRequest.getSeatNumber() + " already exists.");
        }

        Seat newSeat = new Seat();
        newSeat.setBus(bus);
        newSeat.setSeatNumber(seatRequest.getSeatNumber());
        newSeat.setSeatType(seatRequest.getSeatType());
        if (seatRequest.getPriceForSeatType() != null) {
            newSeat.setPriceForSeatType(seatRequest.getPriceForSeatType());
        }
        return new SeatResponse(seatRepository.save(newSeat));
    }

    @Override
    public void deleteSeat(Long seatId) {
        if (!seatRepository.existsById(seatId)) {
            throw new HttpNotFound("Seat not found with id: " + seatId);
        }
        seatRepository.deleteById(seatId);
    }

    @Override
    public List<ScheduleSeatResponse> getSeatStatusForSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new HttpNotFound("Schedule not found with id: " + scheduleId));

        List<Seat> allSeatsOnBus = schedule.getBus().getSeats();

        Set<Long> bookedSeatIds = ticketRepository.findBookedSeatIdsByScheduleId(scheduleId);

        return allSeatsOnBus.stream()
                .map(seat -> new ScheduleSeatResponse(seat, bookedSeatIds.contains(seat.getId())))
                .collect(Collectors.toList());
    }
}