package com.ra.base_spring_boot.model.others;

import com.ra.base_spring_boot.model.base.BaseObject;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "destinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Destination extends BaseObject {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "image", length = 500)
    private String image;

    @Column(name = "wallpaper", length = 500)
    private String wallpaper;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "review_count", columnDefinition = "int default 0")
    private Integer reviewCount = 0;

    @Column(name = "is_featured", columnDefinition = "boolean default false")
    private Boolean isFeatured = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}