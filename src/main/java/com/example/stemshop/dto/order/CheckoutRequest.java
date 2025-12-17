package com.example.stemshop.dto.order;

import jakarta.validation.constraints.*;

public record CheckoutRequest(
        // источник: либо существующая корзина, либо список позиций на странице
        Long[] productIds,
        Integer[] quantities,

        // контактные поля
        @NotBlank String contactName,
        @NotBlank @Pattern(regexp = "^\\+7\\d{10}$", message = "Phone must be +7 and 10 digits") String contactPhone,
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email should be valid")
        String contactEmail,

        // доставка
        @NotBlank String city,
        @NotBlank String address,
        String deliveryComment,

        // платеж
        @NotBlank String paymentMethod, // KASPI_QR / INVOICE_KZ / CARD
        String customerNote
) {}
