package com.peter.tanxuanfood.controller.admin;

import com.peter.tanxuanfood.convert.annotation.ApiMessage;
import com.peter.tanxuanfood.domain.Role;
import com.peter.tanxuanfood.domain.dto.ResultPaginationDTO;
import com.peter.tanxuanfood.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/roles/create")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> handleCreateRole(@Valid @RequestBody Role requestRole) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.roleService.handleCreateRole(requestRole));
    }

    @PutMapping("/roles/update")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> handleUpdateRole(@Valid @RequestBody Role requestRole) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.roleService.handleUpdateRole(requestRole));
    }

    @DeleteMapping("/roles/delete/{id}")
    public ResponseEntity<Void> handleDeleteRole(@PathVariable long id) {
        this.roleService.handleDeleteRole(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(null);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch all role")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.getAllRoles(pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch a role")
    public ResponseEntity<Role> getRole(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.getRoleById(id));
    }



}
