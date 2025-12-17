package com.example.stemshop.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddItemRequest(
        @NotNull Long productId,
        @Min(1) Integer qty // если null — будем считать 1
) {}
