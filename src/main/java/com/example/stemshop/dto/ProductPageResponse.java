package com.example.stemshop.dto;

import java.util.List;

public record ProductPageResponse<T>(
        List<T> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
