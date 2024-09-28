package com.peter.tanxuanfood.service;

import com.peter.tanxuanfood.domain.Role;
import com.peter.tanxuanfood.exception.IdInValidException;
import com.peter.tanxuanfood.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    private static final String ROLE_ERROR = "Role does not exist";

    public Role handleCreateRole(Role requestRole) {
        Role role = new Role();
        role.setName(requestRole.getName());
        role.setDescription(requestRole.getDescription());
        return this.roleRepository.save(role);
    }

    public Role handleUpdateRole(Role requestRole) {
        Role role = this.roleRepository.findById(requestRole.getId()).orElseThrow(() -> new IdInValidException(ROLE_ERROR));
        role.setDescription(requestRole.getDescription());
        return this.roleRepository.save(role);
    }

    public void handleDeleteRole(long id) {
        Role currentRole = this.roleRepository.findById(id).orElseThrow(() -> new IdInValidException(ROLE_ERROR));
        currentRole.getUsers().forEach(user -> user.getRoles().remove(currentRole)); // xóa currentRole khỏi 1 tập user
        this.roleRepository.delete(currentRole);
    }

    public Set<Role> getSetRoleUser(Set<Long> roleIds) {
        return roleIds
                .stream()
                .map(id -> this.roleRepository
                        .findById(id)
                        .orElseThrow(() -> new IdInValidException(ROLE_ERROR)))
                .collect(Collectors.toSet());
    }
}
