package com.example.stemshop.dto.order;

public record OrderItemDto(
        Long productId,
        String name,
        Integer price,
        Integer quantity,
        Integer subtotal
) {}
