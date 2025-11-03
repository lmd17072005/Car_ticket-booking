package com.ra.base_spring_boot.repository.schedule;

import com.ra.base_spring_boot.model.Bus.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface IScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s WHERE s.route.departureStation.id = :departureStationId " +
            "AND s.route.arrivalStation.id = :arrivalStationId " +
            "AND s.departureTime >= :startOfDay AND s.departureTime <= :endOfDay " +
            "AND s.status = 'AVAILABLE'")
    List<Schedule> findSchedulesByCriteria(Long departureStationId,
                                           Long arrivalStationId,
                                           LocalDateTime startOfDay,
                                           LocalDateTime endOfDay);
}