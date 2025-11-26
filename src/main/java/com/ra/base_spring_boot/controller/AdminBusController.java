package com.ra.base_spring_boot.controller;


import com.ra.base_spring_boot.dto.PageResponse;
import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.bus.BusAdminResponse;
import com.ra.base_spring_boot.dto.bus.BusRequest;
import com.ra.base_spring_boot.dto.bus.BusResponse;
import com.ra.base_spring_boot.model.constants.BusStatus;
import com.ra.base_spring_boot.model.constants.BusType;
import com.ra.base_spring_boot.services.bus.IBusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/buses")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminBusController {
    private final IBusService busService;


    @GetMapping
    public ResponseEntity<ResponseWrapper<PageResponse<BusAdminResponse>>> getAllBusesForAdmin(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BusStatus status,
            @RequestParam(required = false) BusType type,
            @PageableDefault(page = 0, size = 10, sort = "id,desc") Pageable pageable) {

        Page<BusAdminResponse> busPage = busService.findAllAdmin(search, status, type ,pageable);
        return ResponseEntity.ok(
                ResponseWrapper.<PageResponse<BusAdminResponse>>builder()
                        .status(HttpStatus.OK)
                        .data(PageResponse.fromPage(busPage))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<BusResponse>> createBus(@Valid @RequestBody BusRequest busRequest) {
        return new ResponseEntity<>(
                ResponseWrapper.<BusResponse>builder()
                        .status(HttpStatus.CREATED)
                        .data(busService.save(busRequest))
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<BusResponse>> updateBus(
            @PathVariable Long id,
            @Valid @RequestBody BusRequest busRequest) {
        return ResponseEntity.ok(
                ResponseWrapper.<BusResponse>builder()
                        .status(HttpStatus.OK)
                        .data(busService.update(id, busRequest))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteBus(@PathVariable Long id) {
        busService.delete(id);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .data("Xóa xe bus thành công")
                        .build()
        );
    }
}
