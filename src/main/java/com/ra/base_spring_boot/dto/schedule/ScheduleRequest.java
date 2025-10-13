package com.ra.base_spring_boot.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleRequest {
    @NotNull(message = "ID of route cannot be null")
    private Long routeId;

    @NotNull(message = "ID of bus cannot be null")
    private Long busId;

    @NotNull(message = "Departure time cannot be null")
    @Future(message = "Departure time must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    private LocalDateTime departureTime;
}
