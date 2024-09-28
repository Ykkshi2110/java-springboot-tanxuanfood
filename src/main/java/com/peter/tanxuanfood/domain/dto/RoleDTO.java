package com.peter.tanxuanfood.domain.dto;

import com.peter.tanxuanfood.type.RoleType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RoleDTO {
    private long id;
    private RoleType name;
}
