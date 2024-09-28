package com.peter.tanxuanfood.controller;

import com.peter.tanxuanfood.domain.ApiResponse;
import com.peter.tanxuanfood.domain.Role;
import com.peter.tanxuanfood.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/roles/update")
    public ResponseEntity<ApiResponse<Role>> handleUpdateRole(@Valid @RequestBody Role requestRole) {
        Role role = this.roleService.handleUpdateRole(requestRole);
        ApiResponse<Role> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(HttpStatus.OK.value());
        apiResponse.setData(role);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/roles/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> handleDeleteRole(@PathVariable long id) {
        this.roleService.handleDeleteRole(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(HttpStatus.NO_CONTENT.value());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
}
