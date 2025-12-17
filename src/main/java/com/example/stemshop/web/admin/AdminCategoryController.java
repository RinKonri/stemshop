package com.example.stemshop.web.admin;

import com.example.stemshop.dto.admin.CategoryUpsertRequest;
import com.example.stemshop.service.admin.AdminCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/admin/categories")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class AdminCategoryController {
    private final AdminCategoryService service;
    public AdminCategoryController(AdminCategoryService s){ this.service=s; }

    @PostMapping public ResponseEntity<Long> create(@Valid @RequestBody CategoryUpsertRequest r){ return ResponseEntity.ok(service.create(r)); }
    @PutMapping("/{id}") public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryUpsertRequest r){ service.update(id,r); return ResponseEntity.noContent().build(); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){ service.delete(id); return ResponseEntity.noContent().build(); }
}
