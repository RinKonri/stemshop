package com.example.stemshop.service.admin;

import com.example.stemshop.domain.Role;
import com.example.stemshop.domain.User;
import com.example.stemshop.repo.RoleRepository;
import com.example.stemshop.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Service
public class AdminUserService {
    private final UserRepository users; private final RoleRepository roles;
    public AdminUserService(UserRepository u, RoleRepository r){ this.users=u; this.roles=r; }

    @Transactional
    public void updateRoles(Long userId, Set<String> roleNames){
        User user = users.findById(userId).orElseThrow();
        user.getRoles().clear();
        for (String rn : roleNames) {
            Role r = roles.findByName(rn).orElseThrow(() -> new IllegalArgumentException("Unknown role: " + rn));
            user.getRoles().add(r);
        }
        users.save(user);
    }
}
