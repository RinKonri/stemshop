package com.example.stemshop.service.admin;

import com.example.stemshop.domain.*;
import com.example.stemshop.domain.orders.Order;
import com.example.stemshop.domain.orders.OrderStatusHistory;
import com.example.stemshop.dto.admin.*;
import com.example.stemshop.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class AdminOrderService {

    private final OrderRepository orders;
    private final OrderItemRepository orderItems;
    private final OrderStatusHistoryRepository history;
    private final ShippingRepository shipping;
    private final PaymentRepository payments; // сделай JpaRepository<Payment, Long>

    public AdminOrderService(OrderRepository o, OrderItemRepository oi, OrderStatusHistoryRepository h, ShippingRepository s, PaymentRepository p) {
        this.orders=o; this.orderItems=oi; this.history=h; this.shipping=s; this.payments=p;
    }

    @Transactional
    public List<Order> list() { return orders.findAll(); }

    @Transactional
    public Order detail(Long id) { return orders.findById(id).orElseThrow(); }

    @Transactional
    public void updateStatus(Long id, String newStatus) {
        Order o = orders.findById(id).orElseThrow();
        String old = o.getStatus();
        if (!old.equals(newStatus)) {
            o.setStatus(newStatus);
            o.setUpdatedAt(Instant.now());
            orders.save(o);
            history.save(OrderStatusHistory.builder()
                    .orderId(o.getId()).oldStatus(old).newStatus(newStatus).changedAt(Instant.now()).build());
        }
    }

    @Transactional
    public void updateShipping(Long orderId, UpdateShippingRequest r) {
        Shipping sh = shipping.findAll().stream()
                .filter(s -> s.getOrderId().equals(orderId))
                .findFirst().orElseThrow();
        if (r.address()!=null) sh.setAddress(r.address());
        if (r.city()!=null) sh.setCity(r.city());
        if (r.postalCode()!=null) sh.setPostalCode(r.postalCode());
        if (r.country()!=null) sh.setCountry(r.country());
        if (r.shippingMethod()!=null) sh.setShippingMethod(r.shippingMethod());
        if (r.trackingNumber()!=null) sh.setTrackingNumber(r.trackingNumber());
        if (r.shippingStatus()!=null) sh.setShippingStatus(r.shippingStatus());
        shipping.save(sh);
    }

    @Transactional
    public void addOrUpdatePayment(Long orderId, UpdatePaymentRequest r) {
        Payment p = new Payment();
        p.setOrderId(orderId);
        p.setAmount(r.amount());
        p.setPaymentMethod(r.paymentMethod());
        p.setPaymentStatus(r.paymentStatus());
        p.setTransactionId(r.transactionId());
        p.setCreatedAt(Instant.now());
        payments.save(p);

        if ("SUCCESS".equalsIgnoreCase(r.paymentStatus())) {
            updateStatus(orderId, "PAID");
        }
    }
}
