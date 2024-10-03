package com.peter.tanxuanfood.controller;

import com.peter.tanxuanfood.convert.annotation.ApiMessage;
import com.peter.tanxuanfood.convert.util.SecurityUtil;
import com.peter.tanxuanfood.domain.dto.LoginDTO;
import com.peter.tanxuanfood.domain.dto.ResLoginDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;


    @PostMapping("/auth/login")
    @ApiMessage("Login a user")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO){
        // Nạp username and password vào Security Context
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        // Xác thực người dùng => cần viết hàm loadByUserName()
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Lưu data của người dùng đã đăng nhập vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // create a token
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        String accessToken = this.securityUtil.createToken(authentication);
        resLoginDTO.setAccessToken(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(resLoginDTO);
    }
}
