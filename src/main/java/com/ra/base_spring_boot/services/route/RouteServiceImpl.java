package com.ra.base_spring_boot.services.route;


import com.ra.base_spring_boot.dto.route.RouteRequest;
import com.ra.base_spring_boot.dto.route.RouteResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Bus.Route;
import com.ra.base_spring_boot.model.Bus.Station;
import com.ra.base_spring_boot.repository.route.IStationRepository;
import com.ra.base_spring_boot.repository.route.IRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements IRouteService {
    private final IRouteRepository routeRepository;
    private final IStationRepository stationRepository;

    @Override
    public List<RouteResponse> findAll() {
        return routeRepository.findAll().stream().map(RouteResponse::new).collect(Collectors.toList());
    }

    @Override
    public RouteResponse findById(Long id) {
        Route route = routeRepository.findById(id).orElseThrow(() -> new HttpNotFound("Route not found with id: " + id));
        return new RouteResponse(route);
    }

    @Override
    public RouteResponse save(RouteRequest routeRequest) {
        if (routeRequest.getDepartureStationId().equals(routeRequest.getArrivalStationId())) {
            throw new HttpBadRequest("Departure and arrival stations must be different");
        }

        Station departureStation = stationRepository.findById(routeRequest.getDepartureStationId())
                .orElseThrow(() -> new HttpNotFound("Departure station not found with id: " + routeRequest.getDepartureStationId()));
        Station arrivalStation = stationRepository.findById(routeRequest.getArrivalStationId())
                .orElseThrow(() -> new HttpNotFound("Arrival station not found with id: " + routeRequest.getArrivalStationId()));

        Route newRoute = new Route();
        newRoute.setDepartureStation(departureStation);
        newRoute.setArrivalStation(arrivalStation);
        newRoute.setPrice(routeRequest.getPrice());
        newRoute.setDuration(routeRequest.getDuration());
        newRoute.setDistance(routeRequest.getDistance());
        return new RouteResponse(routeRepository.save(newRoute));
    }

    @Override
    public RouteResponse update(Long id, RouteRequest routeRequest) {
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Route not found with id: " + id));

        if (routeRequest.getDepartureStationId().equals(routeRequest.getArrivalStationId())) {
            throw new HttpBadRequest("Departure and arrival stations must be different");
        }
        Station departureStation = stationRepository.findById(routeRequest.getDepartureStationId())
                .orElseThrow(() -> new HttpNotFound("Departure station not found with id: " + routeRequest.getDepartureStationId()));
        Station arrivalStation = stationRepository.findById(routeRequest.getArrivalStationId())
                .orElseThrow(() -> new HttpNotFound("Arrival station not found with id: " + routeRequest.getArrivalStationId()));

        existingRoute.setDepartureStation(departureStation);
        existingRoute.setArrivalStation(arrivalStation);
        existingRoute.setPrice(routeRequest.getPrice());
        existingRoute.setDuration(routeRequest.getDuration());
        existingRoute.setDistance(routeRequest.getDistance());
        return new RouteResponse(routeRepository.save(existingRoute));

        }

    @Override
    public void delete(Long id) {
         if (!routeRepository.existsById(id)) {
             throw new HttpNotFound("Route not found with id: " + id);
         }
            routeRepository.deleteById(id);
    }
}
