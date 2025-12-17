package com.example.stemshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email should be valid")
        @Size(min = 4, max = 50, message = "Email must be between 4 and 20 symbols")
        String email,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, max = 100)
        String password,

        @NotBlank(message = "Name field must not be blank")
        @Size(min = 2, max = 30, message = "Name must be between 2 and 30 characters")
        String fullName,

        @NotBlank(message = "Phone number must not be blank")
        @Pattern(
                regexp = "^\\+7(?:\\s?\\d){10}$",
                message = "Phone number must start with +7 and consists of 10 digits"
        )
        String phone
) {}
