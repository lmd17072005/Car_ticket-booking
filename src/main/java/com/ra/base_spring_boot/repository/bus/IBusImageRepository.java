package com.ra.base_spring_boot.repository.bus;

import com.ra.base_spring_boot.model.Bus.BusImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBusImageRepository extends JpaRepository<BusImage, Long> {
}