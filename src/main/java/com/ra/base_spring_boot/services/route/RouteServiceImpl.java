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
        Route route = routeRepository.findById(id).orElseThrow(() -> new HttpNotFound("Không tìm thấy tuyến đường với id: " + id));
        return new RouteResponse(route);
    }

    @Override
    public RouteResponse save(RouteRequest routeRequest) {
        Route newRoute = mapRequestToEntity(new Route(), routeRequest);
        return new RouteResponse(routeRepository.save(newRoute));
    }

    @Override
    public RouteResponse update(Long id, RouteRequest routeRequest) {
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy tuyến đường với id: " + id));

        Route updatedRoute = mapRequestToEntity(existingRoute, routeRequest);
        return new RouteResponse(routeRepository.save(updatedRoute));
    }

    @Override
    public void delete(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new HttpNotFound("Không tìm thấy tuyến đường với id: " + id);
        }
        routeRepository.deleteById(id);
    }

    private Route mapRequestToEntity(Route route, RouteRequest routeRequest) {
        if (routeRequest.getDepartureStationId().equals(routeRequest.getArrivalStationId())) {
            throw new HttpBadRequest("Điểm đi và điểm đến phải khác nhau");
        }

        Station departureStation = stationRepository.findById(routeRequest.getDepartureStationId())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy bến đi với id: " + routeRequest.getDepartureStationId()));
        Station arrivalStation = stationRepository.findById(routeRequest.getArrivalStationId())
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy bến đến với id: " + routeRequest.getArrivalStationId()));



        route.setDepartureStation(departureStation);
        route.setArrivalStation(arrivalStation);
        route.setPrice(routeRequest.getPrice());
        route.setDuration(routeRequest.getDuration());
        route.setDistance(routeRequest.getDistance());


        return route;
    }
}