package com.peter.tanxuanfood.service;

import com.peter.tanxuanfood.domain.Role;
import com.peter.tanxuanfood.domain.User;
import com.peter.tanxuanfood.domain.dto.CreateUserRequest;
import com.peter.tanxuanfood.exception.IdInValidException;
import com.peter.tanxuanfood.repository.RoleRepository;
import com.peter.tanxuanfood.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository
            userRepository;
    private final RoleRepository
            roleRepository;

    public User handleCreateUser(CreateUserRequest createUserRequest) {
        User
                user =
                new User();
        user.setFullName(createUserRequest.getFullName());
        user.setEmail(createUserRequest.getEmail());
        user.setPassword(createUserRequest.getPassword());
        user.setPhone(createUserRequest.getPhone());
        user.setAddress(createUserRequest.getAddress());

        Set<Role> roles = createUserRequest
                .getRequestRoles()
                .stream()
                .map(id -> this.roleRepository
                        .findById(id)
                        .orElseThrow(() -> new IdInValidException("Role does not exists")))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        return this.userRepository.save(user);
    }
}
