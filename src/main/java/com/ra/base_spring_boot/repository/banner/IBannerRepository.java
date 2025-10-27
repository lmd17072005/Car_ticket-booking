package com.ra.base_spring_boot.repository.banner;

import com.ra.base_spring_boot.model.others.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface IBannerRepository extends JpaRepository<Banner, Long>,PagingAndSortingRepository<Banner, Long>  {
    Page<Banner> findByPositionContainingIgnoreCase(String position, Pageable pageable);
}