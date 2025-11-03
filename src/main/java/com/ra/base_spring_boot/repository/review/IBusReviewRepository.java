package com.ra.base_spring_boot.repository.review;

import com.ra.base_spring_boot.model.Bus.Bus;
import com.ra.base_spring_boot.model.Bus.BusReview;
import com.ra.base_spring_boot.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBusReviewRepository extends JpaRepository<BusReview, Long> {
    List<BusReview> findByBus(Bus bus);

    boolean existsByUserAndBus(User user, Bus bus);
}