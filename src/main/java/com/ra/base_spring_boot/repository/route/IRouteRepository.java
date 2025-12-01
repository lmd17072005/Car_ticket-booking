package com.ra.base_spring_boot.repository.route;

import com.ra.base_spring_boot.model.Bus.Route;
import com.ra.base_spring_boot.model.Bus.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByIsPopularTrue();
    Optional<Route> findByDepartureStationAndArrivalStation(Station departure, Station arrival);
}
