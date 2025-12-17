package com.example.stemshop.cart.repo;

import com.example.stemshop.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserIdAndCheckedOutFalse(Long userId);
    Optional<Cart> findByIdAndCheckedOutFalse(UUID id);
}