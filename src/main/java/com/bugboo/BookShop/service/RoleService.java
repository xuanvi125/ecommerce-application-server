package com.bugboo.BookShop.service;

import com.bugboo.BookShop.domain.Role;
import com.bugboo.BookShop.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}
