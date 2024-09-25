package com.peter.tanxuanfood.service;

import com.peter.tanxuanfood.domain.Role;
import com.peter.tanxuanfood.repository.RoleRepository;
import com.peter.tanxuanfood.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role handleCreateRole(Role requestRole) {
        Role role = new Role();
        role.setName(requestRole.getName());
        return this.roleRepository.save(role);
    }
}
