package com.example.stemshop.dto;

import jakarta.validation.constraints.*;

public record LoginRequest(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email should be valid")
        @Size(min = 4, max = 50, message = "Email must be between 4 and 20 symbols")
        String email,
        @NotBlank(message = "Password must not be blank")
        String password
) {}
