package com.example.stemshop.service;

import com.example.stemshop.domain.Role;
import com.example.stemshop.domain.User;
import com.example.stemshop.dto.*;
import com.example.stemshop.repo.RoleRepository;
import com.example.stemshop.repo.UserRepository;
import com.example.stemshop.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, RoleRepository roles, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        Role base = roles.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new IllegalStateException("ROLE_CUSTOMER not found"));

        User u = User.builder()
                .email(email)
                .passwordHash(encoder.encode(req.password()))
                .fullName(req.fullName())
                .phone(req.phone())
                .isActive(true)
                .build();
        u.getRoles().add(base);
        User saved = users.save(u);

        var roleNames = List.of(base.getName());
        String token = jwt.generate(saved.getEmail(), roleNames);
        return new AuthResponse(saved.getId(), saved.getEmail(), roleNames, token, jwt.getExpiresAt());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        String email = req.email().toLowerCase();
        User u = users.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (Boolean.FALSE.equals(u.getIsActive())) {
            throw new IllegalStateException("User is deactivated");
        }
        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        var roleNames = u.getRoles().stream().map(Role::getName).toList();
        String token = jwt.generate(u.getEmail(), roleNames);
        return new AuthResponse(u.getId(), u.getEmail(), roleNames, token, jwt.getExpiresAt());
    }
}