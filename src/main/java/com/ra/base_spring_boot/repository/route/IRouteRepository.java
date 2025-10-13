package com.ra.base_spring_boot.repository.route;

import com.ra.base_spring_boot.model.Bus.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRouteRepository extends JpaRepository<Route, Long> {
}
