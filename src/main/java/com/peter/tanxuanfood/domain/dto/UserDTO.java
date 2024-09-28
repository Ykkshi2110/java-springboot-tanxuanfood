package com.peter.tanxuanfood.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserDTO {
    private long id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private Set<RoleDTO> roles;
}
