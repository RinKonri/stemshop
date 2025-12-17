package com.example.stemshop.dto.admin;

import jakarta.validation.constraints.*;

public record CategoryUpsertRequest(
        @NotBlank String name,
        @NotBlank String slug,
        Long parentId
) {}
