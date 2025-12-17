package com.example.stemshop.cart.domain;

import com.example.stemshop.cart.domain.CartItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "carts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cart {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Schema(description = "id", example = "1")
    private UUID id;

    @Column(name = "user_id")
    @Schema(description = "id", example = "1")
    private Long userId; // null для гостя

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "is_checked_out", nullable = false)
    private boolean checkedOut;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<CartItem> items = new HashSet<>();
}