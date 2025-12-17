package com.example.stemshop.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record UpdateQtyRequest(
        @NotNull Long productId,
        @Min(0) Integer qty // 0 = удалить позицию
) {}