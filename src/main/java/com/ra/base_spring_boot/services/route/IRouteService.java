package com.ra.base_spring_boot.services.route;

import com.ra.base_spring_boot.dto.route.RouteRequest;
import com.ra.base_spring_boot.dto.route.RouteResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRouteService {
    List<RouteResponse> findAll();
    RouteResponse findById(Long id);
    RouteResponse save(RouteRequest routeRequest);
    RouteResponse update(Long id, RouteRequest routeRequest);
    void delete(Long id);
    List<RouteResponse> findPopular();

    RouteResponse uploadImage(Long routeId, MultipartFile imageFile);

}
