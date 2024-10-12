package com.peter.tanxuanfood.controller.admin;

import com.peter.tanxuanfood.convert.annotation.ApiMessage;
import com.peter.tanxuanfood.convert.util.SecurityUtil;
import com.peter.tanxuanfood.domain.User;
import com.peter.tanxuanfood.domain.dto.LoginDTO;
import com.peter.tanxuanfood.domain.dto.ResLoginDTO;
import com.peter.tanxuanfood.exception.IdInValidException;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final ModelMapper modelMapper;

    private static final String REFRESH_TOKEN = "refreshToken";

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
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO.getUserLogin());
        resLoginDTO.setAccessToken(accessToken);

        // create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);

        // update user
        this.userService.updateUserToken(refreshToken, loginDTO.getUsername());

        // set Cookies
        ResponseCookie responseCookie = ResponseCookie
                .from(REFRESH_TOKEN, refreshToken)
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

    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(@CookieValue(name = REFRESH_TOKEN, defaultValue = "abc") String refreshToken) {
        if (refreshToken.equals("abc")) {
            throw new IdInValidException("You don't have a refresh token in cookies");
        }
        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        // check user by token + email
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new IdInValidException("Refresh Token is valid");
        }

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUserName(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = this.mapToUserLogin(currentUserDB);
            resLoginDTO.setUserLogin(userLogin);
        }
        String accessToken = this.securityUtil.createAccessToken(email, resLoginDTO.getUserLogin());
        resLoginDTO.setAccessToken(accessToken);

        // create refresh token
        String newRefreshToken = this.securityUtil.createRefreshToken(email, resLoginDTO);

        // update user
        this.userService.updateUserToken(refreshToken, email);

        // set Cookies
        ResponseCookie responseCookie = ResponseCookie
                .from(REFRESH_TOKEN, newRefreshToken)
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

    @PostMapping("/auth/logout")
    @ApiMessage("Logout user")
    public ResponseEntity<Void> logout() throws IdInValidException {
        String email = SecurityUtil
                .getCurrentUserLogin()
                .orElse("");
        if (email.isEmpty()) {
            throw new IdInValidException("Access token is in valid");
        }

        // update refreshToken = null
        this.userService.updateUserToken(null, email);

        // remove refreshToken cookies
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from(REFRESH_TOKEN, "")
                .httpOnly(true)
                .path("/api/v1")
                .secure(true)
                .maxAge(0)
                .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .build();
    }

}
