package com.example.stemshop.web.admin;

import com.example.stemshop.dto.admin.ProductUpsertRequest;
import com.example.stemshop.service.admin.AdminProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/admin/products")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class AdminProductController {

    private final AdminProductService service;
    public AdminProductController(AdminProductService s){ this.service=s; }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody ProductUpsertRequest req){
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody ProductUpsertRequest req){
        service.update(id, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
