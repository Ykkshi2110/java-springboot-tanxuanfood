package com.peter.tanxuanfood.service;

import com.peter.tanxuanfood.domain.Meta;
import com.peter.tanxuanfood.domain.Role;
import com.peter.tanxuanfood.domain.User;
import com.peter.tanxuanfood.domain.dto.CreateUserRequest;
import com.peter.tanxuanfood.domain.dto.ResultPaginationDTO;
import com.peter.tanxuanfood.domain.dto.UserDTO;
import com.peter.tanxuanfood.exception.IdInValidException;
import com.peter.tanxuanfood.repository.UserRepository;
import com.querydsl.core.types.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper;

    public User handleCreateUser(@Valid CreateUserRequest createUserRequest) {
        if (this.userRepository.existsByEmail(createUserRequest.getEmail()))
            throw new IdInValidException("Email already in use");
        User user = new User();
        user.setFullName(createUserRequest.getFullName());
        user.setEmail(createUserRequest.getEmail());
        user.setPassword(createUserRequest.getPassword());
        user.setPhone(createUserRequest.getPhone());
        user.setAddress(createUserRequest.getAddress());

        Set<Long> roleIds = createUserRequest
                .getRoles()
                .stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
        Set<Role> roles = this.roleService.getSetRoleUser(roleIds);
        user.setRoles(roles);
        return this.userRepository.save(user);
    }

    public User handleUpdateUser(User user) {
        User currentUser = this.userRepository
                .findById(user.getId())
                .orElseThrow(() -> new IdInValidException("User does not exist"));
        currentUser.setFullName(user.getFullName());
        currentUser.setAddress(user.getAddress());
        currentUser.setPhone(user.getPhone());

        Set<Long> roleIds = user
                .getRoles()
                .stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
        Set<Role> roles = this.roleService.getSetRoleUser(roleIds);
        currentUser.setRoles(roles);
        return this.userRepository.save(currentUser);
    }

    public void handleDeleteUser(long id) {
        User currentUser = this.userRepository.findById(id).orElseThrow(() -> new IdInValidException("User does not exist"));
        this.userRepository.delete(currentUser);
    }

    public ResultPaginationDTO fetchAllUser(Pageable pageable){
        Page<User> pageUsers = this.userRepository.findAll(pageable);
        Page<UserDTO> pageUserDTOs = pageUsers.map(element -> modelMapper.map(element, UserDTO.class));
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUserDTOs.getTotalPages());
        meta.setTotal(pageUserDTOs.getTotalElements());
        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setData(pageUserDTOs.getContent());
        return result;
    }

}
