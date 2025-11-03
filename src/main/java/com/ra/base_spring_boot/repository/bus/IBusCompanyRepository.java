package com.ra.base_spring_boot.repository.bus;

import com.ra.base_spring_boot.model.Bus.BusCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBusCompanyRepository extends JpaRepository<BusCompany, Long>, PagingAndSortingRepository<BusCompany, Long> {
    Page<BusCompany> findByCompanyNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsByCompanyNameIgnoreCase(String companyName);
}