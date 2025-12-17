package com.example.stemshop.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String article,
        Integer price,
        Integer stock,
        Long brandId,
        Double rating,
        Integer ratingCount,
        String photo
) {}
