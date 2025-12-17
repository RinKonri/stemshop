package com.example.stemshop.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId; // связь по FK (простым id; при желании можно сделать @ManyToOne<Order>)

    @Column(nullable = false)
    private Integer amount; // в копейках/тиынах

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod; // KASPI_QR / INVOICE_KZ / CARD ...

    @Column(name = "payment_status", nullable = false, length = 50)
    private String paymentStatus; // PENDING / SUCCESS / FAILED / REFUNDED

    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
