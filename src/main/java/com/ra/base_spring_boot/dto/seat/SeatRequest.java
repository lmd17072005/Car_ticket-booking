package com.ra.base_spring_boot.dto.seat;

import com.ra.base_spring_boot.model.constants.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SeatRequest {
    @NotNull(message = "Bus ID cannot be null")
    private Long busId;

    @NotBlank(message = "Number of seat cannot be blank")
    private String seatNumber;

    @NotNull(message = "Type of seat cannot be null")
    private SeatType seatType;

    // Giá phụ thu, có thể là null (mặc định là 0 trong Entity)
    private BigDecimal priceForSeatType;
}