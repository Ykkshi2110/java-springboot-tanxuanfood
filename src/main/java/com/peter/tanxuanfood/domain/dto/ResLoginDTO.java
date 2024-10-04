package com.peter.tanxuanfood.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ResLoginDTO {
    private String accessToken;
    private UserLogin userLogin;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class UserLogin{
        private long id;
        private String email;
        private String name;
    }

}
