package com.peter.tanxuanfood.controller;

import com.peter.tanxuanfood.convert.annotation.ApiMessage;
import com.peter.tanxuanfood.convert.util.SecurityUtil;
import com.peter.tanxuanfood.domain.User;
import com.peter.tanxuanfood.domain.dto.LoginDTO;
import com.peter.tanxuanfood.domain.dto.ResLoginDTO;
import com.peter.tanxuanfood.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Value("${peter.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;


    public ResLoginDTO.UserLogin mapToUserLogin(User currentUser) {
        TypeMap<User, ResLoginDTO.UserLogin> typeMap = modelMapper.getTypeMap(User.class, ResLoginDTO.UserLogin.class);
//            Dùng get để kiểm tra thử đã có typeMap nào tồn tại chưa, nếu chưa thì create
        if (typeMap == null) {
            typeMap = modelMapper.createTypeMap(User.class, ResLoginDTO.UserLogin.class);
            typeMap.addMappings(mapper -> mapper.map(User::getFullName, ResLoginDTO.UserLogin::setName));
        }
        return modelMapper.map(currentUser, ResLoginDTO.UserLogin.class);
    }


    @PostMapping("/auth/login")
    @ApiMessage("Login a user")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // Nạp username and password vào Security Context
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        // Xác thực người dùng => cần viết hàm loadByUserName()
        Authentication authentication = authenticationManagerBuilder
                .getObject()
                .authenticate(authenticationToken);

        // Lưu data của người dùng đã đăng nhập vào SecurityContext
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
        // create a token
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUserName(loginDTO.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = this.mapToUserLogin(currentUserDB);
            resLoginDTO.setUserLogin(userLogin);
        } else {
            resLoginDTO.setUserLogin(null);
        }
        String accessToken = this.securityUtil.createAccessToken(authentication, resLoginDTO.getUserLogin());
        resLoginDTO.setAccessToken(accessToken);

        // create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);

        // update user
        this.userService.updateUserToken(refreshToken, loginDTO.getUsername());

        // set Cookies
        ResponseCookie responseCookie = ResponseCookie
                .from("refreshToken", refreshToken)
                .secure(true)
                .httpOnly(true)
                .path("/api/v1")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Fetch account user")
    public ResponseEntity<ResLoginDTO.UserLogin> fetchAccount() {
        String email = SecurityUtil
                .getCurrentUserLogin()
                .orElse("");
        User currentUserDB = this.userService.handleGetUserByUserName(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        if (currentUserDB != null) {
            userLogin = this.mapToUserLogin(currentUserDB);
        }
        return ResponseEntity
                .ok()
                .body(userLogin);
    }
}
