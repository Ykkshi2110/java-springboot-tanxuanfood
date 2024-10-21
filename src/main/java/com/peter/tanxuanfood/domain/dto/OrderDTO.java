package com.peter.tanxuanfood.domain.dto;

import com.peter.tanxuanfood.type.StatusType;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
public class OrderDTO {
    private long id;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private StatusType status;
    private double totalPrice;
}
