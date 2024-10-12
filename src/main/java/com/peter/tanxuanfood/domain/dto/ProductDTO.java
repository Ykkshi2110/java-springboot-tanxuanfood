package com.peter.tanxuanfood.domain.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private long id;
    private String name;
    private String description;
    private double price;
    private long stockQuantity;
}
