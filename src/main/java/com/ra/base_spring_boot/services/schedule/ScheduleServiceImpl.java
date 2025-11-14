package com.ra.base_spring_boot.services.schedule;

import com.ra.base_spring_boot.dto.payment.CalculatedCancellationMilestone;
import com.ra.base_spring_boot.dto.schedule.ScheduleRequest;
import com.ra.base_spring_boot.dto.schedule.ScheduleResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Bus;
import com.ra.base_spring_boot.model.Bus.BusReview;
import com.ra.base_spring_boot.model.Bus.Route;
import com.ra.base_spring_boot.model.Bus.Schedule;
import com.ra.base_spring_boot.model.constants.ScheduleStatus;
import com.ra.base_spring_boot.model.constants.SeatType;
import com.ra.base_spring_boot.model.payment.CancellationPolicy;
import com.ra.base_spring_boot.repository.bus.IBusRepository;
import com.ra.base_spring_boot.repository.payment.ICancellationPolicyRepository;
import com.ra.base_spring_boot.repository.review.IBusReviewRepository;
import com.ra.base_spring_boot.repository.route.IRouteRepository;
import com.ra.base_spring_boot.repository.schedule.IScheduleRepository;
import com.ra.base_spring_boot.specification.ScheduleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ra.base_spring_boot.exception.HttpConflict;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements IScheduleService {
    private final IScheduleRepository scheduleRepository;
    private final IRouteRepository routeRepository;
    private final IBusRepository busRepository;
    private final IBusReviewRepository busReviewRepository;
    private final ICancellationPolicyRepository cancellationPolicyRepository;


    @Override
    public List<ScheduleResponse> findAll() {
        return scheduleRepository.findAll().stream().map(this::mapToScheduleResponse).collect(Collectors.toList());
    }

    @Override
    public ScheduleResponse findById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Schedule not found with id: " + id));
        return mapToScheduleResponse(schedule);
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

    @Override
    public ScheduleResponse update(Long id, ScheduleRequest scheduleRequest) {
        Schedule existingSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy lịch trình với ID: " + id));

        if (existingSchedule.getAvailableSeats() < existingSchedule.getTotalSeats()) {
            throw new HttpConflict("Không thể sửa lịch trình vì đã có vé được bán.");
        }

        Route route = routeRepository.findById(scheduleRequest.getRouteId())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy tuyến đường với ID: " + scheduleRequest.getRouteId()));
        Bus bus = busRepository.findById(scheduleRequest.getBusId())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy xe bus với ID: " + scheduleRequest.getBusId()));

        existingSchedule.setRoute(route);
        existingSchedule.setBus(bus);
        existingSchedule.setDepartureTime(scheduleRequest.getDepartureTime());
        LocalDateTime arrivalTime = scheduleRequest.getDepartureTime().plusMinutes(route.getDuration());
        existingSchedule.setArrivalTime(arrivalTime);
        existingSchedule.setTotalSeats(bus.getCapacity());
        existingSchedule.setAvailableSeats(bus.getCapacity());

        return new ScheduleResponse(scheduleRepository.save(existingSchedule));
    }

    @Override
    public void cancelSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy lịch trình với ID: " + id));
        if (schedule.getAvailableSeats() < schedule.getTotalSeats()) {
            throw new HttpConflict("Không thể xóa lịch trình vì đã có vé được bán. Hãy chuyển sang trạng thái Hủy.");
        }

        schedule.setStatus(ScheduleStatus.CANCELLED);
        scheduleRepository.save(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScheduleResponse> searchSchedules(
            Long departureStationId, Long arrivalStationId, String departureDate,
            Integer fromHour, Integer toHour, BigDecimal maxPrice, List<Long> companyIds, List<SeatType> seatTypes,
            Pageable pageable) {

        LocalDateTime startOfDay, endOfDay;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(departureDate, formatter);
            startOfDay = date.atStartOfDay();
            endOfDay = date.atTime(LocalTime.MAX);
        } catch (DateTimeParseException e) {
            throw new HttpBadRequest("Định dạng ngày không hợp lệ. Vui lòng sử dụng yyyy-MM-dd.");
        }

        Specification<Schedule> spec = ScheduleSpecification.searchByCriteria(
                departureStationId, arrivalStationId, startOfDay, endOfDay,
                fromHour, toHour, maxPrice, companyIds, seatTypes);

        Page<Schedule> schedulePage = scheduleRepository.findAll(spec, pageable);

        return schedulePage.map(this::mapToScheduleResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScheduleResponse> findSchedulesByCompany(Long companyId, String departureDate, Pageable pageable) {
        LocalDateTime start, end;
        if (departureDate != null && !departureDate.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(departureDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                start = date.atStartOfDay();
                end = date.atTime(LocalTime.MAX);
            } catch (DateTimeParseException e) {
                throw new HttpBadRequest("Định dạng ngày không hợp lệ. Vui lòng sử dụng yyyy-MM-dd.");
            }
        } else {
            start = LocalDateTime.now();
            end = start.plusHours(24);
        }

        Page<Schedule> schedulePage = scheduleRepository.findByBus_Company_IdAndDepartureTimeBetween(companyId, start, end, pageable);
        return schedulePage.map(this::mapToScheduleResponse);
    }

    private ScheduleResponse mapToScheduleResponse(Schedule schedule) {
        ScheduleResponse response = new ScheduleResponse(schedule);

        List<BusReview> reviews = busReviewRepository.findByBus(schedule.getBus());
        double avgRating = reviews.stream()
                .mapToInt(BusReview::getRating)
                .average()
                .orElse(0.0);
        avgRating = Math.round(avgRating * 10.0) / 10.0;
        response.setAverageRating(avgRating);
        response.setTotalRatings(reviews.size());

        response.setCancellationMilestones(calculateMilestones(schedule));

        return response;
    }

    private List<CalculatedCancellationMilestone> calculateMilestones(Schedule schedule) {
        List<CalculatedCancellationMilestone> milestones = new ArrayList<>();

        List<CancellationPolicy> generalPolicies = cancellationPolicyRepository.findByRouteIsNull();

        if (generalPolicies == null || generalPolicies.isEmpty()) {
            return milestones;
        }

        List<CancellationPolicy> sortedPolicies = generalPolicies.stream()
                .sorted(Comparator.comparing(CancellationPolicy::getCancellationTimeLimit).reversed())
                .toList();

        LocalDateTime previousDeadline = null;

        for (CancellationPolicy policy : sortedPolicies) {
            CalculatedCancellationMilestone milestone = new CalculatedCancellationMilestone();
            LocalDateTime deadline = schedule.getDepartureTime().minusMinutes(policy.getCancellationTimeLimit());

            milestone.setDeadline(deadline);
            milestone.setCancellationFeePercentage(100 - policy.getRefundPercentage());

            if (previousDeadline == null) {
                milestone.setStartTimeDescription("Sau khi đặt");
            } else {
                milestone.setStartTime(previousDeadline);
            }
            milestones.add(milestone);
            previousDeadline = deadline;
        }

        if (previousDeadline != null) {
            CalculatedCancellationMilestone finalMilestone = new CalculatedCancellationMilestone();
            finalMilestone.setStartTime(previousDeadline);
            finalMilestone.setDeadlineDescription("Giờ khởi hành");
            finalMilestone.setCancellationFeePercentage(100);
            milestones.add(finalMilestone);
        }

        return milestones;
    }
}