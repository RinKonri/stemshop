package com.example.stemshop.dto;

import java.util.List;

public record AuthResponse(
        Long userId,
        String email,
        List<String> roles,
        String accessToken,
        long expiresAt // millis epoch
) {}
