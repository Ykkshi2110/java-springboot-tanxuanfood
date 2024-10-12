package com.peter.tanxuanfood.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddProductRequest {
    @NotNull
    private long productId;

    @NotNull
    private long quantity;
}
