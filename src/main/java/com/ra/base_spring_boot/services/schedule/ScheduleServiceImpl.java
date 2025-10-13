package com.ra.base_spring_boot.services.schedule;

import com.ra.base_spring_boot.dto.schedule.ScheduleRequest;
import com.ra.base_spring_boot.dto.schedule.ScheduleResponse;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Bus;
import com.ra.base_spring_boot.model.Bus.Route;
import com.ra.base_spring_boot.model.Bus.Schedule;
import com.ra.base_spring_boot.model.constants.ScheduleStatus;
import com.ra.base_spring_boot.repository.bus.IBusRepository;
import com.ra.base_spring_boot.repository.route.IRouteRepository;
import com.ra.base_spring_boot.repository.schedule.IScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements IScheduleService {
    private final IScheduleRepository scheduleRepository;
    private final IRouteRepository routeRepository;
    private final IBusRepository busRepository;

    @Override
    public List<ScheduleResponse> findAll() {
        return
                scheduleRepository.findAll().stream().map(ScheduleResponse::new).collect(Collectors.toList());
    }

    @Override
    public ScheduleResponse findById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Schedule not found with id: " + id));
        return new ScheduleResponse(schedule);
    }

    @Override
    public ScheduleResponse save(ScheduleRequest scheduleRequest) {
        Route route = routeRepository.findById(scheduleRequest.getRouteId())
                .orElseThrow(() -> new HttpNotFound("Route not found with id: " + scheduleRequest.getRouteId()));
        Bus bus = busRepository.findById(scheduleRequest.getBusId())
                .orElseThrow(() -> new HttpNotFound("Bus not found with id: " + scheduleRequest.getBusId()));

        Schedule newSchedule = new Schedule();
        newSchedule.setRoute(route);
        newSchedule.setBus(bus);
        newSchedule.setDepartureTime(scheduleRequest.getDepartureTime());
        LocalDateTime arrivalTime = scheduleRequest.getDepartureTime().plusMinutes(route.getDuration());
        newSchedule.setArrivalTime(arrivalTime);

        newSchedule.setTotalSeats(bus.getCapacity());
        newSchedule.setAvailableSeats(bus.getCapacity());
        newSchedule.setStatus(ScheduleStatus.AVAILABLE);

        return new ScheduleResponse(scheduleRepository.save(newSchedule));
    }
}
