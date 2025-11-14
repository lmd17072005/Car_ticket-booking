package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.route.RouteRequest;
import com.ra.base_spring_boot.dto.route.RouteResponse;
import com.ra.base_spring_boot.services.route.IRouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/routes")
@RequiredArgsConstructor
public class RouteController {
    private final IRouteService routeService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<RouteResponse>>> getAllRoutes() {
        return ResponseEntity.ok(ResponseWrapper.
                <List<RouteResponse>>builder().status(HttpStatus.OK).data(routeService.findAll()).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<RouteResponse>> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseWrapper.
                <RouteResponse>builder().status(HttpStatus.OK).data(routeService.findById(id)).build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<RouteResponse>> createRoute(@Valid @RequestBody RouteRequest routeRequest) {
        return new ResponseEntity<>(ResponseWrapper.
                <RouteResponse>builder().status(HttpStatus.CREATED).data(routeService.save(routeRequest)).build(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<RouteResponse>> updateRoute(@PathVariable Long id, @Valid @RequestBody RouteRequest routeRequest) {
        return ResponseEntity.ok(ResponseWrapper.
                <RouteResponse>builder().status(HttpStatus.OK).data(routeService.update(id, routeRequest)).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<String>> deleteRoute(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.ok(ResponseWrapper.
                <String>builder().status(HttpStatus.OK).data("Route deleted successfully").build());
    }

    @GetMapping("/popular")
    public ResponseEntity<ResponseWrapper<List<RouteResponse>>> getPopularRoutes() {
        return ResponseEntity.ok(ResponseWrapper.
                <List<RouteResponse>>builder().status(HttpStatus.OK).data(routeService.findPopular()).build());
    }
}
