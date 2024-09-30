package com.peter.tanxuanfood.controller;

import com.peter.tanxuanfood.convert.annotation.ApiMessage;
import com.peter.tanxuanfood.domain.ApiResponse;
import com.peter.tanxuanfood.domain.User;
import com.peter.tanxuanfood.domain.dto.CreateUserRequest;
import com.peter.tanxuanfood.domain.dto.ResultPaginationDTO;
import com.peter.tanxuanfood.domain.dto.UserDTO;
import com.peter.tanxuanfood.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserDTO mapToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    @PostMapping("/users/create")
    @ApiMessage("Create a user")
    public ResponseEntity<UserDTO> handleCreateUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        User user = this.userService.handleCreateUser(createUserRequest);
        UserDTO userDTO = this.mapToDTO(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDTO);
    }

    @PutMapping("/users/update")
    @ApiMessage("Update a user")
    public ResponseEntity<UserDTO> handleUpdateUser(@Valid @RequestBody User user) {
        User currentUser = this.userService.handleUpdateUser(user);
        UserDTO userDTO = this.mapToDTO(currentUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDTO);
    }

    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> handleDeleteUser(@PathVariable Long id) {
        this.userService.handleDeleteUser(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(null);
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all user")
    public ResponseEntity<ResultPaginationDTO> fetchAllUsers(@RequestParam Optional<String> name, Pageable pageable) {
        ResultPaginationDTO resultPaginationDTO = this.userService.fetchAllUser(name, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resultPaginationDTO);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch a user")
    public ResponseEntity<UserDTO> fetchUserById(@PathVariable Long id) {
        UserDTO userDTO = this.userService.fetchUserById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDTO);
    }

}
