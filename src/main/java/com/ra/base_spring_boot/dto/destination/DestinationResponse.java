package com.ra.base_spring_boot.dto.destination;

import com.ra.base_spring_boot.model.others.Destination;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DestinationResponse {
    private Long id;
    private String name;
    private String location;
    private String image;
    private String wallpaper;
    private String description;
    private Integer reviewCount;
    private Boolean isFeatured;

    public static DestinationResponse fromEntity(Destination destination) {
        return DestinationResponse.builder()
                .id(destination.getId())
                .name(destination.getName())
                .location(destination.getLocation())
                .image(destination.getImage())
                .wallpaper(destination.getWallpaper())
                .description(destination.getDescription())
                .reviewCount(destination.getReviewCount())
                .isFeatured(destination.getIsFeatured())
                .build();
    }
}