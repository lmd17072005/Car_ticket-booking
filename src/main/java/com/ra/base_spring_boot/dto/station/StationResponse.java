package com.ra.base_spring_boot.dto.station;

import com.ra.base_spring_boot.model.Bus.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class StationResponse {
    private Long id;
    private String name;
    private String image;
    private String wallpaper;
    private String descriptions;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StationResponse fromEntity(Station station) {
        return StationResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .image(station.getImage())
                .wallpaper(station.getWallpaper())
                .descriptions(station.getDescriptions())
                .location(station.getLocation())
                .createdAt(station.getCreatedAt())
                .updatedAt(station.getUpdatedAt())
                .build();
    }
}