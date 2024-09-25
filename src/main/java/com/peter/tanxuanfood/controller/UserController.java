package com.peter.tanxuanfood.controller;

import com.peter.tanxuanfood.domain.ApiResponse;
import com.peter.tanxuanfood.domain.User;
import com.peter.tanxuanfood.domain.dto.CreateUserRequest;
import com.peter.tanxuanfood.service.UserService;
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
public class UserController {
    private final UserService userService;

    @PostMapping("/users/create")
    public ResponseEntity<ApiResponse<User>> handleCreateUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        User user = this.userService.handleCreateUser(createUserRequest);
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(HttpStatus.CREATED.value());
        apiResponse.setData(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

}
