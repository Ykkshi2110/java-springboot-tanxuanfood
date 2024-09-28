package com.peter.tanxuanfood.controller;

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
    public ResponseEntity<ApiResponse<UserDTO>> handleCreateUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        User user = this.userService.handleCreateUser(createUserRequest);
        UserDTO userDTO = this.mapToDTO(user);
        ApiResponse<UserDTO> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(HttpStatus.CREATED.value());
        apiResponse.setData(userDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @PutMapping("/users/update")
    public ResponseEntity<ApiResponse<UserDTO>> handleUpdateUser(@Valid @RequestBody User user) {
        User currentUser = this.userService.handleUpdateUser(user);
        UserDTO userDTO = this.mapToDTO(currentUser);
        ApiResponse<UserDTO> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(HttpStatus.OK.value());
        apiResponse.setData(userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> handleDeleteUser(@PathVariable Long id) {
        this.userService.handleDeleteUser(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(HttpStatus.NO_CONTENT.value());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }

    @GetMapping("users")
    public ResponseEntity<ApiResponse<ResultPaginationDTO>> fetchAllUsers(Pageable pageable) {
        ResultPaginationDTO resultPaginationDTO = this.userService.fetchAllUser(pageable);
        ApiResponse<ResultPaginationDTO> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(HttpStatus.OK.value());
        apiResponse.setData(resultPaginationDTO);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

}
