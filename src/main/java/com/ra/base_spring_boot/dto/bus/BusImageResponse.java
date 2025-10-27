package com.ra.base_spring_boot.dto.bus;

import com.ra.base_spring_boot.model.Bus.BusImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusImageResponse {
    private Long id;
    private String imageUrl;
    private Long busId;

    public BusImageResponse(BusImage busImage) {
        this.id = busImage.getId();
        this.imageUrl = busImage.getImageUrl();
        if (busImage.getBus() != null) {
            this.busId = busImage.getBus().getId();
        }
    }
}
