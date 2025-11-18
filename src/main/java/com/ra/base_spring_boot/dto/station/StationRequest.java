package com.ra.base_spring_boot.dto.station;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StationRequest {

    @NotBlank(message = "Station name is required")
    private String name;

    private String image;
    private String wallpaper;
    private String descriptions;

    @NotBlank(message = "Station location is required")
    private String location;

    private Boolean isPopular = false;
}