package com.ra.base_spring_boot.repository.route;


import com.ra.base_spring_boot.model.Bus.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface IStationRepository extends JpaRepository<Station, Long>, PagingAndSortingRepository<Station, Long> {


    @Query("SELECT s FROM Station s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.location) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Station> searchByNameOrLocation(@Param("search") String search, Pageable pageable);
    boolean existsByNameIgnoreCase(String name);
    List<Station> findByIsPopularTrue();
}
