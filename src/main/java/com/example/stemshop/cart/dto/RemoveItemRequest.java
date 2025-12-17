package com.example.stemshop.cart.dto;

import jakarta.validation.constraints.NotNull;

public record RemoveItemRequest(
        @NotNull Long productId
) {}