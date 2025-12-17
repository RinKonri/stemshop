package com.example.stemshop.service;

import com.example.stemshop.cart.repo.*;
import com.example.stemshop.domain.*;
import com.example.stemshop.domain.orders.*;
import com.example.stemshop.dto.order.*;
import com.example.stemshop.repo.*;
import com.example.stemshop.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class PublicCheckoutService {

    private final UserRepository users;
    private final ProductRepository products;
    private final OrderRepository orders;
    private final OrderItemRepository orderItems;
    private final OrderStatusHistoryRepository history;
    private final CartRepository carts;
    private final ShippingRepository shipping;

    public PublicCheckoutService(UserRepository users,
                                 ProductRepository products,
                                 OrderRepository orders,
                                 OrderItemRepository orderItems,
                                 OrderStatusHistoryRepository history,
                                 CartRepository carts,
                                 ShippingRepository shipping) {
        this.users = users;
        this.products = products;
        this.orders = orders;
        this.orderItems = orderItems;
        this.history = history;
        this.carts = carts;
        this.shipping = shipping;
    }

    @Transactional
    public CheckoutPreviewDto checkout(CheckoutRequest req) {
        // для кого
        Long userId = SecurityUtil.currentEmail()
                .flatMap(e -> users.findByEmail(e).map(User::getId))
                .orElse(null); // null = гость

        // позиции (из запроса или из корзины авторизованного)
        List<long[]> rows = new ArrayList<>();
        if (req.productIds() != null && req.quantities() != null && req.productIds().length == req.quantities().length) {
            for (int i = 0; i < req.productIds().length; i++) rows.add(new long[]{req.productIds()[i], req.quantities()[i]});
        } else if (userId != null) {
            var cart = carts.findByUserIdAndCheckedOutFalse(userId)
                    .orElseThrow(() -> new NoSuchElementException("Cart is empty"));
            cart.getItems().forEach(ci -> rows.add(new long[]{ci.getProductId(), ci.getQty()}));
        } else {
            throw new IllegalArgumentException("No items provided");
        }
        if (rows.isEmpty()) throw new IllegalArgumentException("No items");

        // проверка склада + суммы
        int itemsTotal = 0;
        List<OrderItem> toPersist = new ArrayList<>();
        List<OrderItemDto> view = new ArrayList<>();

        for (var r : rows) {
            long pid = r[0];
            int qty = (int) r[1];
            var p = products.findById(pid).orElseThrow(() -> new NoSuchElementException("Product not found: " + pid));
            if (p.getStock() < qty) throw new IllegalStateException("Not enough stock for product id=" + pid);
            p.setStock(p.getStock() - qty); // просто уменьшаем (для простоты)
            products.save(p);

            int price = p.getPrice();
            itemsTotal += price * qty;

            toPersist.add(OrderItem.builder()
                    .productId(p.getId())
                    .price(price)
                    .quantity(qty)
                    .build());

            view.add(new OrderItemDto(p.getId(), p.getName(), price, qty, price * qty));
        }

        int deliveryPrice = 2000;
        int total = itemsTotal + deliveryPrice;

        // создаём заказ
        Order order = Order.builder()
                .userId(userId) // может быть null (гость)
                .totalPrice(total)
                .status("PENDING")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        // контактные/платёжные поля (добавлены в V1)
        order.setContactName(req.contactName());
        order.setContactPhone(req.contactPhone());
        order.setContactEmail(req.contactEmail());
        order.setPaymentMethod(req.paymentMethod());
        order.setCustomerNote(req.customerNote());

        order = orders.save(order);

        // позиции заказа
        Order saved = orders.save(order);

        for (var oi : toPersist) {
            oi.setOrder(saved);
        }
        orderItems.saveAll(toPersist);

        // история
        history.save(OrderStatusHistory.builder()
                .orderId(saved.getId())
                .oldStatus(null)
                .newStatus("PENDING")
                .changedAt(Instant.now())
                .build());

        // доставка
        shipping.save(Shipping.builder()
                .orderId(saved.getId())
                .address(req.address())
                .city(req.city())
                .shippingMethod("COURIER")
                .shippingStatus("PENDING")
                .build());

        // если оформлял авторизованный — закрыть его активную корзину
        if (userId != null) {
            carts.findByUserIdAndCheckedOutFalse(userId).ifPresent(c -> {
                c.setCheckedOut(true);
                c.getItems().clear();
                carts.save(c);
            });
        }

        // временная ссылка на оплату
        String paymentUrl = "https://pay.example/checkout?orderId=" + saved.getId() + "&method=" + req.paymentMethod();

        return new CheckoutPreviewDto(
                saved.getId(),
                req.contactName(), req.contactPhone(), req.contactEmail(),
                req.city(), req.address(),
                req.paymentMethod(),
                itemsTotal, deliveryPrice, total,
                view,
                paymentUrl
        );
    }
}
