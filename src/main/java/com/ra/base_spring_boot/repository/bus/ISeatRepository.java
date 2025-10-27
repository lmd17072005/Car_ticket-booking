package com.ra.base_spring_boot.repository.bus;

import com.ra.base_spring_boot.model.Bus.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISeatRepository extends JpaRepository<Seat, Long>  {
}
