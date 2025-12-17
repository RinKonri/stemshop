package com.example.stemshop.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String article; // артикул

    @Column(nullable = false)
    private Integer price; // в копейках/тиынах

    @Column(columnDefinition = "text")
    private String photo;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "technical_characteristics", columnDefinition = "text")
    private String technicalCharacteristics;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "brand_id")
    private Long brandId; // FK на brands.id

    @Column(name = "rating")
    private Double rating;

    @Column(name = "rating_count")
    private Integer ratingCount;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
        if (rating == null) rating = (double) 0;
        if (ratingCount == null) ratingCount = 0;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
