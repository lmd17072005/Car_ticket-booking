package com.ra.base_spring_boot.repository.schedule;

import com.ra.base_spring_boot.model.Bus.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IScheduleRepository extends JpaRepository<Schedule, Long>, JpaSpecificationExecutor<Schedule> {
    Page<Schedule> findByBus_Company_IdAndDepartureTimeBetween(Long companyId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
}