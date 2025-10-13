package com.ra.base_spring_boot.repository.schedule;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ra.base_spring_boot.model.Bus.Schedule;

public interface IScheduleRepository extends JpaRepository<Schedule, Long> {
}
