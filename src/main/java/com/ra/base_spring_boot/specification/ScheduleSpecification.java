package com.ra.base_spring_boot.specification;



import com.ra.base_spring_boot.model.Bus.Route;
import com.ra.base_spring_boot.model.Bus.Schedule;
import com.ra.base_spring_boot.model.constants.BusStatus;
import com.ra.base_spring_boot.model.constants.ScheduleStatus;
import com.ra.base_spring_boot.model.constants.SeatType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleSpecification {

    public static Specification<Schedule> hasDepartureDate(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            return criteriaBuilder.between(root.get("departureTime"), startOfDay, endOfDay);
        };
    }

    public static Specification<Schedule> hasStatus(ScheduleStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }

            LocalDateTime now = LocalDateTime.now();

            switch (status) {
                case UPCOMING:
                    // Tìm những chuyến chưa chạy và chưa bị hủy
                    return cb.and(
                            root.get("status").in(ScheduleStatus.UPCOMING, ScheduleStatus.FULL),
                            cb.greaterThan(root.get("departureTime"), now)
                    );
                case RUNNING:
                    // Tìm những chuyến có thời gian hiện tại nằm giữa giờ đi và giờ đến, và chưa bị hủy
                    return cb.and(
                            cb.notEqual(root.get("status"), ScheduleStatus.CANCELLED),
                            cb.lessThanOrEqualTo(root.get("departureTime"), now),
                            cb.greaterThan(root.get("arrivalTime"), now)
                    );
                case COMPLETED:
                    // Tìm những chuyến đã qua giờ đến và chưa bị hủy
                    return cb.and(
                            cb.notEqual(root.get("status"), ScheduleStatus.CANCELLED),
                            cb.lessThanOrEqualTo(root.get("arrivalTime"), now)
                    );
                case CANCELLED:
                case FULL:
                    // Với các trạng thái này, chỉ cần tìm chính xác trong DB
                    return cb.equal(root.get("status"), status);
                default:
                    return cb.conjunction();
            }
        };
    }

    public static Specification<Schedule> hasStation(Long stationId) {
        return (root, query, criteriaBuilder) -> {
            if (stationId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Schedule, Route> routeJoin = root.join("route");
            Predicate departureMatch = criteriaBuilder.equal(routeJoin.get("departureStation").get("id"), stationId);
            Predicate arrivalMatch = criteriaBuilder.equal(routeJoin.get("arrivalStation").get("id"), stationId);
            return criteriaBuilder.or(departureMatch, arrivalMatch);
        };
    }

    public static Specification<Schedule> isBusActive() {
        return (root, query, cb) -> {
            return cb.equal(root.join("bus").get("status"), BusStatus.ACTIVE);
        };
    }

    public static Specification<Schedule> searchByCriteria(
            Long departureStationId, Long arrivalStationId,
            LocalDateTime startOfDay, LocalDateTime endOfDay,
            Integer fromHour, Integer toHour,
            BigDecimal maxPrice, List<Long> companyIds, List<SeatType> seatTypes) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("route").get("departureStation").get("id"), departureStationId));
            predicates.add(cb.equal(root.get("route").get("arrivalStation").get("id"), arrivalStationId));
            predicates.add(cb.between(root.get("departureTime"), startOfDay, endOfDay));
            predicates.add(root.get("status").in(ScheduleStatus.UPCOMING, ScheduleStatus.FULL));
            predicates.add(cb.greaterThan(root.get("availableSeats"), 0));

            if (fromHour != null) {
                predicates.add(cb.greaterThanOrEqualTo(cb.function("HOUR", Integer.class, root.get("departureTime")), fromHour));
            }
            if (toHour != null) {
                predicates.add(cb.lessThanOrEqualTo(cb.function("HOUR", Integer.class, root.get("departureTime")), toHour));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("route").get("price"), maxPrice));
            }
            if (companyIds != null && !companyIds.isEmpty()) {
                predicates.add(root.get("bus").get("company").get("id").in(companyIds));
            }
            if (seatTypes != null && !seatTypes.isEmpty()) {
                query.distinct(true);
                predicates.add(root.join("bus").join("seats").get("seatType").in(seatTypes));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}