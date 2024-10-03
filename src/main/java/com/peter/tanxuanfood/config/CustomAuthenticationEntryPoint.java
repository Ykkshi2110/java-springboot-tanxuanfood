package com.peter.tanxuanfood.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peter.tanxuanfood.domain.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();
    private final ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        this.delegate.commence(request, response, authException); // Vẫn đảm bảo Header vẫn hiện lỗi nếu có lỗi
        response.setContentType("application/json;charset=UTF-8"); // Set kiểu data c hỗ trợ tiếng việt

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        apiResponse.setError(authException.getCause().getMessage());
        apiResponse.setMessage("Token invalid or expired");
        mapper.writeValue(response.getWriter(), apiResponse);
    }
}
