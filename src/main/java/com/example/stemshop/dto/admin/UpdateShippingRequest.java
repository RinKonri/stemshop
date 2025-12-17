package com.example.stemshop.dto.admin;
public record UpdateShippingRequest(
        String address, String city, String postalCode, String country,
        String shippingMethod, String trackingNumber, String shippingStatus // PENDING/IN_TRANSIT/DELIVERED/RETURNED
) {}
