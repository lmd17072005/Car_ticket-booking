package com.ra.base_spring_boot.specification;

import com.ra.base_spring_boot.model.Bus.Schedule;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ra.base_spring_boot.model.Bus.Schedule;
import com.ra.base_spring_boot.model.constants.SeatType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleSpecification {

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
            predicates.add(cb.equal(root.get("status"), "AVAILABLE"));
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