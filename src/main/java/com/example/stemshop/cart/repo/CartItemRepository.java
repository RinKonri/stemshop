package com.example.stemshop.cart.repo;

import com.example.stemshop.cart.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCart_IdAndProductId(java.util.UUID cartId, Long productId);
    void deleteByCart_IdAndProductId(java.util.UUID cartId, Long productId);
}