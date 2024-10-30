package com.peter.tanxuanfood.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public abstract class PaymentDTO {
    private VNPayResponse vnPayResponse;

    @Data
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor
    @Builder
    public static class VNPayResponse {
        private String code;
        private String message;

        @JsonInclude(JsonInclude.Include.NON_DEFAULT) // ignore null and empty value
        private String paymentURL;
    }
}
