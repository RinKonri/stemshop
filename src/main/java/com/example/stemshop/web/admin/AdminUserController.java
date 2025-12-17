package com.example.stemshop.web.admin;

import com.example.stemshop.dto.admin.UpdateUserRolesRequest;
import com.example.stemshop.service.admin.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final AdminUserService service;
    public AdminUserController(AdminUserService service){ this.service = service; }

    @PatchMapping("/{userId}/roles")
    public ResponseEntity<Void> updateRoles(@PathVariable Long userId, @RequestBody UpdateUserRolesRequest req){
        service.updateRoles(userId, req.roles());
        return ResponseEntity.noContent().build();
    }
}
