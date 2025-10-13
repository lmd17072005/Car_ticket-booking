package com.ra.base_spring_boot.repository.route;

import com.ra.base_spring_boot.model.Bus.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IStationRepository extends PagingAndSortingRepository<Station, Long>, JpaRepository<Station, Long> {
    // Search by name (containing)
    Page<Station> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Search by location (containing)
    Page<Station> findByLocationContainingIgnoreCase(String location, Pageable pageable);

    // Combined search (name OR location)
    @Query("SELECT s FROM Station s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.location) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Station> searchByNameOrLocation(String search, Pageable pageable);

    // Find by ID for single CRUD
    Optional<Station> findById(Long id);
}

