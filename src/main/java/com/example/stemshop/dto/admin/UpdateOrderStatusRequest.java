package com.example.stemshop.dto.admin;
import jakarta.validation.constraints.NotBlank;
public record UpdateOrderStatusRequest(@NotBlank String newStatus) {} // PENDING/PAID/SHIPPED/COMPLETED/CANCELLED
