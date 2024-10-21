package com.peter.tanxuanfood.domain.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
@Data
public class ProductFilter {
    private String name;

   @Nullable
    private double minPrice;

   @Nullable
    private double maxPrice;

   @Nullable
    private Boolean availableProduct; // dùng Boolean vì boolean mặc định không truyền vào sẽ là false
}
