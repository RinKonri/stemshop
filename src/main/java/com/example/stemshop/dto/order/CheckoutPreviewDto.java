package com.example.stemshop.dto.order;

import java.util.List;

public record CheckoutPreviewDto(
        Long orderId,
        String contactName,
        String contactPhone,
        String contactEmail,
        String city,
        String address,
        String paymentMethod,
        int itemsTotal,
        int deliveryPrice,
        int total,
        List<OrderItemDto> items,
        String paymentUrl  // временный URL на оплату
) {}
