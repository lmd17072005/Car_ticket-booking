package com.ra.base_spring_boot.dto.bus;

import com.ra.base_spring_boot.model.Bus.BusCompany;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BusCompanyResponse {
    private Long id;
    private String companyName;
    private String image;
    private String descriptions;
    private Boolean isPopular;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BusCompanyResponse(BusCompany busCompany) {
        this.id = busCompany.getId();
        this.companyName = busCompany.getCompanyName();
        this.image = busCompany.getImage();
        this.descriptions = busCompany.getDescriptions();
        this.isPopular = busCompany.getIsPopular();
        this.createdAt = busCompany.getCreatedAt();
        this.updatedAt = busCompany.getUpdatedAt();
    }
}