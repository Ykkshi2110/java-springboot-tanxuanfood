package com.peter.tanxuanfood.controller.payment;

import com.peter.tanxuanfood.convert.annotation.ApiMessage;
import com.peter.tanxuanfood.domain.dto.PaymentDTO;
import com.peter.tanxuanfood.service.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final VnpayService vnpayService;

    @GetMapping("/create-payment")
    @ApiMessage("Create a payment")
    public ResponseEntity<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(this.vnpayService.createVnPayPayment(request));
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<PaymentDTO.VNPayResponse> callback(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        if(status.equals("00")) {
            return ResponseEntity.status(HttpStatus.OK).body(new PaymentDTO.VNPayResponse("00", "Success", ""));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}