package com.ra.base_spring_boot.services.schedule;

import com.ra.base_spring_boot.dto.payment.CalculatedCancellationMilestone;
import com.ra.base_spring_boot.dto.schedule.ScheduleRequest;
import com.ra.base_spring_boot.dto.schedule.ScheduleResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.*;
import com.ra.base_spring_boot.model.constants.BusStatus;
import com.ra.base_spring_boot.model.constants.ScheduleStatus;
import com.ra.base_spring_boot.model.constants.SeatType;
import com.ra.base_spring_boot.model.payment.CancellationPolicy;
import com.ra.base_spring_boot.repository.bus.IBusRepository;
import com.ra.base_spring_boot.repository.payment.ICancellationPolicyRepository;
import com.ra.base_spring_boot.repository.review.IBusReviewRepository;
import com.ra.base_spring_boot.repository.route.IRouteRepository;
import com.ra.base_spring_boot.repository.route.IStationRepository;
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
    private final IStationRepository stationRepository;



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
    @Transactional
    public ScheduleResponse save(ScheduleRequest request) {
        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy xe với ID: " + request.getBusId()));

        if (bus.getStatus() != BusStatus.ACTIVE) {
            throw new HttpConflict("Không thể tạo lịch trình. Xe " + bus.getLicensePlate() + " đang ở trạng thái " + bus.getStatus() + ".");
        }

        Station departureStation = stationRepository.findById(request.getDepartureStationId())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy điểm đi với ID: " + request.getDepartureStationId()));

        Station arrivalStation = stationRepository.findById(request.getArrivalStationId())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy điểm đến với ID: " + request.getArrivalStationId()));

        Route route = routeRepository.findByDepartureStationAndArrivalStation(departureStation, arrivalStation)
                .orElseGet(() -> {
                    Route newRoute = new Route();
                    newRoute.setDepartureStation(departureStation);
                    newRoute.setArrivalStation(arrivalStation);
                    newRoute.setDuration(180);
                    return routeRepository.save(newRoute);
                });

        Schedule newSchedule = new Schedule();
        newSchedule.setBus(bus);
        newSchedule.setRoute(route);
        newSchedule.setDepartureTime(request.getDepartureTime());
        newSchedule.setPrice(request.getPrice());


        if (request.getStatus() != null && request.getStatus() == ScheduleStatus.CANCELLED) {
            newSchedule.setStatus(ScheduleStatus.CANCELLED);
        } else {
            newSchedule.setStatus(ScheduleStatus.UPCOMING);
        }

        if (route.getDuration() == null || route.getDuration() <= 0) {
            throw new HttpBadRequest("Tuyến đường chưa được cấu hình thời gian di chuyển (duration).");
        }
        LocalDateTime arrivalTime = request.getDepartureTime().plusMinutes(route.getDuration());
        newSchedule.setArrivalTime(arrivalTime);

        newSchedule.setTotalSeats(bus.getCapacity());
        newSchedule.setAvailableSeats(bus.getCapacity());

        Schedule savedSchedule = scheduleRepository.save(newSchedule);
        return mapToScheduleResponse(savedSchedule);
    }

    @Override
    @Transactional
    public ScheduleResponse update(Long id, ScheduleRequest request) {
        Schedule existingSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy lịch trình với ID: " + id));

        if (existingSchedule.getDepartureTime().isBefore(LocalDateTime.now()) &&
                existingSchedule.getStatus() != ScheduleStatus.CANCELLED) {
            throw new HttpConflict("Không thể sửa lịch trình đã hoặc đang chạy.");
        }

        if (existingSchedule.getAvailableSeats() < existingSchedule.getTotalSeats()) {
            throw new HttpConflict("Không thể sửa lịch trình vì đã có vé được bán.");
        }

        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy xe với ID: " + request.getBusId()));

        if (bus.getStatus() != BusStatus.ACTIVE) {
            throw new HttpConflict("Không thể cập nhật lịch trình. Xe " + bus.getLicensePlate() + " đang ở trạng thái " + bus.getStatus() + ".");
        }

        Station departureStation = stationRepository.findById(request.getDepartureStationId())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy điểm đi với ID: " + request.getDepartureStationId()));
        Station arrivalStation = stationRepository.findById(request.getArrivalStationId())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy điểm đến với ID: " + request.getArrivalStationId()));
        Route route = routeRepository.findByDepartureStationAndArrivalStation(departureStation, arrivalStation)
                .orElseGet(() -> {
                    Route newRoute = new Route();
                    newRoute.setDepartureStation(departureStation);
                    newRoute.setArrivalStation(arrivalStation);
                    newRoute.setDuration(180);
                    return routeRepository.save(newRoute);
                });

        existingSchedule.setBus(bus);
        existingSchedule.setRoute(route);
        existingSchedule.setDepartureTime(request.getDepartureTime());
        existingSchedule.setPrice(request.getPrice());


        if (request.getStatus() != null) {
            if (request.getStatus() == ScheduleStatus.UPCOMING || request.getStatus() == ScheduleStatus.CANCELLED) {
                existingSchedule.setStatus(request.getStatus());
            }
        }

        if (route.getDuration() == null || route.getDuration() <= 0) {
            throw new HttpBadRequest("Tuyến đường chưa được cấu hình thời gian di chuyển (duration).");
        }
        existingSchedule.setArrivalTime(request.getDepartureTime().plusMinutes(route.getDuration()));
        existingSchedule.setTotalSeats(bus.getCapacity());
        existingSchedule.setAvailableSeats(bus.getCapacity());

        Schedule updatedSchedule = scheduleRepository.save(existingSchedule);
        return mapToScheduleResponse(updatedSchedule);
    }


    @Override
    public void cancelSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy lịch trình với ID: " + id));
        if (schedule.getArrivalTime().isBefore(LocalDateTime.now())) {
            throw new HttpConflict("Không thể hủy lịch trình đã hoàn thành.");
        }

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

    @Override
    @Transactional(readOnly = true)
    public Page<ScheduleResponse> findAllForAdmin(LocalDate date, ScheduleStatus status, Long stationId, Pageable pageable) {
        Specification<Schedule> spec = Specification
                .where(ScheduleSpecification.isBusActive())
                .and(ScheduleSpecification.hasDepartureDate(date))
                .and(ScheduleSpecification.hasStatus(status))
                .and(ScheduleSpecification.hasStation(stationId));

        return scheduleRepository.findAll(spec, pageable).map(this::mapToScheduleResponse);
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