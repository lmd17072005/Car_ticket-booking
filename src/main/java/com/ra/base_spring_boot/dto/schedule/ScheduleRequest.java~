package com.ra.base_spring_boot.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ra.base_spring_boot.model.constants.ScheduleStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleRequest {
    @NotNull(message = "Điểm đi không được để trống")
    private Long departureStationId;

    @NotNull(message = "Điểm đến không được để trống")
    private Long arrivalStationId;

    @NotNull(message = "Xe không được để trống")
    private Long busId;

    @NotNull(message = "Thời gian khởi hành không được để trống")
    @Future(message = "Thời gian khởi hành phải ở trong tương lai")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departureTime;

    @NotNull(message = "Giá vé không được để trống")
    @Min(value = 0, message = "Giá vé không được âm")
    private BigDecimal price;

    private ScheduleStatus status = ScheduleStatus.UPCOMING;
}
