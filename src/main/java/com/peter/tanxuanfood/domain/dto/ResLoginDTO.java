package com.peter.tanxuanfood.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ResLoginDTO {
    private String accessToken;

}