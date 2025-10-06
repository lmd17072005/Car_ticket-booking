package com.ra.base_spring_boot.model.others;

import com.ra.base_spring_boot.model.base.BaseObject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "banners")
@Getter
@Setter
public class Banner extends BaseObject {

    @Column(name = "banner_url", nullable = false, length = 255)
    private String bannerUrl;

    @Column(name = "position", length = 100)
    private String position;
}