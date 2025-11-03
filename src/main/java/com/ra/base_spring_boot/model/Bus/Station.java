package com.ra.base_spring_boot.model.Bus;

import com.ra.base_spring_boot.model.base.BaseObject;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "stations")
@Getter
@Setter
@NoArgsConstructor   // Cần thiết cho JPA và Builder
@AllArgsConstructor  // Cần thiết cho Builder
@Builder             // Annotation để tạo phương thức .builder()
public class Station extends BaseObject {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "image", length = 255)
    private String image;

    @Column(name = "wallpaper", length = 255)
    private String wallpaper;

    @Column(name = "descriptions", columnDefinition = "longtext")
    private String descriptions;

    @Column(name = "location", length = 255)
    private String location;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}