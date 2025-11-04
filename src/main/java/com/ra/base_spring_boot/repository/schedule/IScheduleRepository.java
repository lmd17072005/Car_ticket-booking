package com.ra.base_spring_boot.repository.schedule;

import com.ra.base_spring_boot.model.Bus.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IScheduleRepository extends JpaRepository<Schedule, Long>, JpaSpecificationExecutor<Schedule> {

    @Query("SELECT s FROM Schedule s WHERE s.bus.company.id = :companyId " +
            "AND s.departureTime >= :startTime AND s.departureTime <= :endTime AND s.status = 'AVAILABLE'")
    Page<Schedule> findSchedulesByCompany(Long companyId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
}