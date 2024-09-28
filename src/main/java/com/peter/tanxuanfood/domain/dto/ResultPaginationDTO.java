package com.peter.tanxuanfood.domain.dto;

import com.peter.tanxuanfood.domain.Meta;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDTO {
    private Meta meta;
    private Object data;
}
