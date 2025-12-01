package com.ra.base_spring_boot.services.schedule;

import com.ra.base_spring_boot.dto.schedule.ScheduleRequest;
import com.ra.base_spring_boot.dto.schedule.ScheduleResponse;
import com.ra.base_spring_boot.model.constants.ScheduleStatus;
import com.ra.base_spring_boot.model.constants.SeatType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IScheduleService {
    List<ScheduleResponse> findAll();
    ScheduleResponse findById(Long id);
    ScheduleResponse save(ScheduleRequest scheduleRequest);
    ScheduleResponse update(Long id, ScheduleRequest scheduleRequest);
    void cancelSchedule(Long id);

    Page<ScheduleResponse> searchSchedules(
            Long departureStationId, Long arrivalStationId, String departureDate,
            Integer fromHour, Integer toHour, BigDecimal maxPrice, List<Long> companyIds, List<SeatType> seatTypes,
            Pageable pageable
    );

    Page<ScheduleResponse> findSchedulesByCompany(Long companyId, String departureDate, Pageable pageable);
    Page<ScheduleResponse> findAllForAdmin(LocalDate date, ScheduleStatus status, Long stationId, Pageable pageable);
}