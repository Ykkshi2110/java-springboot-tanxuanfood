package com.peter.tanxuanfood.service;

import com.peter.tanxuanfood.domain.Meta;
import com.peter.tanxuanfood.domain.Role;
import com.peter.tanxuanfood.domain.dto.ResultPaginationDTO;
import com.peter.tanxuanfood.domain.dto.RoleDTO;
import com.peter.tanxuanfood.exception.IdInValidException;
import com.peter.tanxuanfood.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    private static final String ROLE_ERROR = "Role does not exist";
    private final ModelMapper modelMapper;

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

    public Role getRoleById(long id) {
        return this.roleRepository.findById(id).orElseThrow(() -> new IdInValidException(ROLE_ERROR));
    }

    public ResultPaginationDTO getAllRoles(Pageable pageable) {
        Page<Role> rolePages = this.roleRepository.findAll(pageable);
        Page<RoleDTO> roleDTOPages = rolePages.map(element -> modelMapper.map(element, RoleDTO.class));
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(roleDTOPages.getTotalPages());
        meta.setTotal(roleDTOPages.getTotalElements());
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setData(roleDTOPages.getContent());
        return resultPaginationDTO;
    }
}
