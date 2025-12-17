package com.example.stemshop.dto.admin;

import jakarta.validation.constraints.*;
import java.util.Set;

public record ProductUpsertRequest(
        @NotBlank String name,
        @NotBlank String article,
        @NotNull @Min(0) Integer price,
        String photo,
        String description,
        String technicalCharacteristics,
        @NotNull @Min(0) Integer stock,
        Long brandId,
        Set<Long> categoryIds // привязки к категориям
) {}
