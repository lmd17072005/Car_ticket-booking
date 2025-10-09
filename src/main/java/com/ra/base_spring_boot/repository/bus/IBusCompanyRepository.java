package com.ra.base_spring_boot.repository.bus;

import com.ra.base_spring_boot.model.Bus.BusCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface IBusCompanyRepository extends JpaRepository<BusCompany, Long> {
    Page<BusCompany> findByCompanyNameContainingIgnoreCase(String name, Pageable pageable);
}
