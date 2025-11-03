package com.ra.base_spring_boot.services.schedule;

import com.ra.base_spring_boot.dto.schedule.ScheduleRequest;
import com.ra.base_spring_boot.dto.schedule.ScheduleResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Bus;
import com.ra.base_spring_boot.model.Bus.BusReview;
import com.ra.base_spring_boot.model.Bus.Route;
import com.ra.base_spring_boot.model.Bus.Schedule;
import com.ra.base_spring_boot.model.constants.ScheduleStatus;
import com.ra.base_spring_boot.repository.bus.IBusRepository;
import com.ra.base_spring_boot.repository.review.IBusReviewRepository;
import com.ra.base_spring_boot.repository.route.IRouteRepository;
import com.ra.base_spring_boot.repository.schedule.IScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements IScheduleService {
    private final IScheduleRepository scheduleRepository;
    private final IRouteRepository routeRepository;
    private final IBusRepository busRepository;
    private final IBusReviewRepository busReviewRepository;

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

    @Override
    public List<ScheduleResponse> searchSchedules(Long departureStationId, Long arrivalStationId, String departureDate) {
        // Chuyển đổi và validate ngày
        LocalDateTime startOfDay;
        LocalDateTime endOfDay;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(departureDate, formatter);
            startOfDay = date.atStartOfDay(); // Ví dụ: 2025-11-15 00:00:00
            endOfDay = date.atTime(LocalTime.MAX);   // Ví dụ: 2025-11-15 23:59:59.999...
        } catch (DateTimeParseException e) {
            throw new HttpBadRequest("Định dạng ngày không hợp lệ. Vui lòng sử dụng yyyy-MM-dd.");
        }

        // Gọi repository để tìm kiếm các lịch trình
        List<Schedule> schedules = scheduleRepository.findSchedulesByCriteria(departureStationId, arrivalStationId, startOfDay, endOfDay);

        // Chuyển đổi sang DTO và tính toán các thông-tin phụ
        return schedules.stream().map(schedule -> {
            // Sử dụng constructor của ScheduleResponse để chuyển đổi
            ScheduleResponse response = new ScheduleResponse(schedule);

            // Tính toán thông-tin đánh giá
            List<BusReview> reviews = busReviewRepository.findByBus(schedule.getBus());
            double avgRating = reviews.stream()
                    .mapToInt(BusReview::getRating)
                    .average()
                    .orElse(0.0); // Trả về 0.0 nếu không có đánh giá nào

            // Làm tròn đến 1 chữ số thập phân
            avgRating = Math.round(avgRating * 10.0) / 10.0;

            // Gán giá trị đã tính vào response
            response.setAverageRating(avgRating);
            response.setTotalRatings(reviews.size());

            return response;
        }).collect(Collectors.toList());
    }
}
