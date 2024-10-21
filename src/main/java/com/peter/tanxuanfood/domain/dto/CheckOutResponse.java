package com.peter.tanxuanfood.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT) // cái nào null thì sẽ không kèm vào
public class CheckOutResponse {
    private Client client;
    private PreCheckOutResponse cart;
    private OrderDTO order;
    private double totalPrice;

    @Data
    @RequiredArgsConstructor
    public static class Client {
        private long id;
        private String fullName;
        private String email;
    }

    @Data
    @RequiredArgsConstructor
    public static class PreCheckOutResponse {
        private long sum;
        private List<ProductDTO> items;
    }
}
