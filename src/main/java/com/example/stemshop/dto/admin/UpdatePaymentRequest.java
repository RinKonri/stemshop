package com.example.stemshop.dto.admin;
public record UpdatePaymentRequest(
        Integer amount, String paymentMethod, String paymentStatus, String transactionId
) {}