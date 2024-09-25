package com.peter.tanxuanfood.controller;

import com.peter.tanxuanfood.domain.ApiResponse;
import com.peter.tanxuanfood.domain.Role;
import com.peter.tanxuanfood.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/roles/create")
    public ResponseEntity<ApiResponse<Role>> handleCreateRole(@Valid @RequestBody Role requestRole) {
        Role role = this.roleService.handleCreateRole(requestRole);
        ApiResponse<Role> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(HttpStatus.CREATED.value());
        apiResponse.setData(role);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }
}
