package com.example.stemshop.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "shipping")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false, columnDefinition = "text")
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 100)
    private String country;

    @Column(name = "shipping_method", nullable = false, length = 50)
    private String shippingMethod; // напр. COURIER / PICKUP / POST

    @Column(name = "tracking_number", length = 255)
    private String trackingNumber;

    @Column(name = "shipping_status", nullable = false, length = 50)
    private String shippingStatus; // PENDING / IN_TRANSIT / DELIVERED / RETURNED

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;
}
