package com.peter.tanxuanfood.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class ResAddProductDTO {
    private long cartId;
    private ResCartDTO cart;

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class ResCartDTO {
        private long sum;
        private List<ProductDTO> items;
    }
}
