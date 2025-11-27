package com.ra.base_spring_boot.repository.bus;

import com.ra.base_spring_boot.model.Bus.BusMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBusMaintenanceRepository extends JpaRepository<BusMaintenance, Long> {
    List<BusMaintenance> findAllByOrderByStartDateDesc();
    List<BusMaintenance> findByBusIdOrderByStartDateDesc(Long busId);
}