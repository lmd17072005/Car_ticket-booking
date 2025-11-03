package com.ra.base_spring_boot.services.schedule;

import com.ra.base_spring_boot.dto.schedule.ScheduleRequest;
import com.ra.base_spring_boot.dto.schedule.ScheduleResponse;
import java.util.List;

public interface IScheduleService {
    List<ScheduleResponse> findAll();
    ScheduleResponse findById(Long id);
    ScheduleResponse save(ScheduleRequest scheduleRequest);

    List<ScheduleResponse> searchSchedules(Long departureStationId, Long arrivalStationId, String departureDate);
}
