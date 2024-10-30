package com.peter.tanxuanfood.service;

import com.peter.tanxuanfood.config.VnpayConfig;
import com.peter.tanxuanfood.convert.util.VNPayUtil;
import com.peter.tanxuanfood.domain.dto.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VnpayService {
    private final VnpayConfig vnpayConfig;
    public PaymentDTO.VNPayResponse createVnPayPayment (HttpServletRequest request){
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnpayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        // build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap,true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap,false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnpayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnpayConfig.getVnpPayUrl() + "?" + queryUrl;
        return PaymentDTO.VNPayResponse.builder().code("ok").message("success").paymentURL(paymentUrl).build();
    }

}