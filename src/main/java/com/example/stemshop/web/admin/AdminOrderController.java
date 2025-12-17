package com.example.stemshop.web.admin;

import com.example.stemshop.domain.orders.Order;
import com.example.stemshop.dto.admin.*;
import com.example.stemshop.service.admin.AdminOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/v1/admin/orders")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class AdminOrderController {

    private final AdminOrderService service;
    public AdminOrderController(AdminOrderService s){ this.service=s; }

    @GetMapping public ResponseEntity<List<Order>> list(){ return ResponseEntity.ok(service.list()); }

    @GetMapping("/{id}") public ResponseEntity<Order> detail(@PathVariable Long id){ return ResponseEntity.ok(service.detail(id)); }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest r){
        service.updateStatus(id, r.newStatus());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/shipping")
    public ResponseEntity<Void> updateShipping(@PathVariable Long id, @RequestBody UpdateShippingRequest r){
        service.updateShipping(id, r);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<Void> addOrUpdatePayment(@PathVariable Long id, @RequestBody UpdatePaymentRequest r){
        service.addOrUpdatePayment(id, r);
        return ResponseEntity.noContent().build();
    }
}
