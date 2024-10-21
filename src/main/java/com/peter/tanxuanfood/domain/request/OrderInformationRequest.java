package com.peter.tanxuanfood.domain.request;

import com.peter.tanxuanfood.type.StatusType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderInformationRequest {
    @NotNull
    private String receiverName;

    @NotNull
    private String receiverAddress;

    @NotNull
    private String receiverPhone;

    @Enumerated(EnumType.STRING)
    private StatusType status;
}
