package com.ra.base_spring_boot.repository.destination;

import com.ra.base_spring_boot.model.others.Destination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IDestinationRepository extends JpaRepository<Destination, Long> {


    List<Destination> findByIsFeaturedTrue();

    boolean existsByNameIgnoreCase(String name);

    Page<Destination> findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String name, String location, Pageable pageable);
}