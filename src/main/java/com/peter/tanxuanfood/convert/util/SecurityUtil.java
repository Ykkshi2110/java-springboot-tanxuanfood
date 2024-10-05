package com.peter.tanxuanfood.convert.util;

import com.nimbusds.jose.util.Base64;
import com.peter.tanxuanfood.domain.dto.ResLoginDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityUtil {

    private final JwtEncoder jwtEncoder;
    private final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${peter.jwt.base64-secret}")
    private String jwtKey;

    @Value("${peter.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${peter.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;


    public String createAccessToken(String email, ResLoginDTO.UserLogin userLogin){
         Instant now = Instant.now();
         Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);


         // @formatter:off
         JwtClaimsSet claims = JwtClaimsSet.builder()
                                           .issuedAt(now)
                                           .expiresAt(validity)
                                           .subject(email)
                                           .claim("user", userLogin)
                                           .build();
         JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
         return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
     }

    public String createRefreshToken(String email, ResLoginDTO resLoginDTO){
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);


        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                                          .issuedAt(now)
                                          .expiresAt(validity)
                                          .subject(email)
                                          .claim("user", resLoginDTO.getUserLogin())
                                          .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public static Optional<String> getCurrentUserLogin(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                       .filter(authentication -> authentication.getCredentials() instanceof String)
                       .map(authentication -> (String) authentication.getCredentials());

    }

    public SecretKey getSecretKey(){
        byte[] keyBytes = Base64
                .from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    public Jwt checkValidRefreshToken(String token){
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(JWT_ALGORITHM).build();
        try{
            return jwtDecoder.decode(token);
        } catch (Exception e){
            logger.error(">>> Refresh Token error: {}", e.getMessage());
            throw  e;
        }
    }


}
