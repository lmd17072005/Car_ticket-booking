package com.ra.base_spring_boot.dto.station;

import com.ra.base_spring_boot.model.Bus.Station;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationResponse {

    private Long id;
    private String name;
    private String location;
    private String descriptions;
    private String image;
    private String wallpaper;
    private Boolean isPopular;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StationResponse fromEntity(Station station) {
        return StationResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .location(station.getLocation())
                .descriptions(station.getDescriptions())
                .image(station.getImage())
                .wallpaper(station.getWallpaper())
                .isPopular(station.getIsPopular())
                .createdAt(station.getCreatedAt())
                .updatedAt(station.getUpdatedAt())
                .build();
    }
}