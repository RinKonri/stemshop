package com.example.stemshop.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

// Утилита для получения userId из SecurityContext

public final class SecurityUtil {
    private SecurityUtil() {}

    public static Optional<Long> currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return Optional.empty();
        Object principal = auth.getPrincipal();
        if (principal instanceof Long id) return Optional.of(id);
        return Optional.empty();
    }
    public static Optional<String> currentEmail() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null) return Optional.empty();
        Object p = a.getPrincipal();
        return (p instanceof String s && !s.isBlank()) ? Optional.of(s) : Optional.empty();
    }
}