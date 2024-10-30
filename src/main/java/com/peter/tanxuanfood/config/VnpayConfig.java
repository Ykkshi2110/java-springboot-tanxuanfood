package com.peter.tanxuanfood.config;

import com.peter.tanxuanfood.convert.util.VNPayUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
public class VnpayConfig {
    @Getter
    @Value("${payment.vnPay.url}")
    private String vnpPayUrl;

    @Value("${payment.vnPay.returnUrl}")
    private String vnpReturnUrl;

    @Value("${payment.vnPay.tmnCode}")
    private String vnpTmnCode;

    @Getter
    @Value("${payment.vnPay.secretKey}")
    private String secretKey;

    @Value("${payment.vnPay.version}")
    private String vnpVersion;

    @Value("${payment.vnPay.command}")
    private String vnpCommand;

    @Value("${payment.vnPay.orderType}")
    private String orderType;

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnpVersion);
        vnpParamsMap.put("vnp_Command", this.vnpCommand);
        vnpParamsMap.put("vnp_TmnCode", this.vnpTmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_TxnRef",  VNPayUtil.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" +  VNPayUtil.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderType", this.orderType);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", this.vnpReturnUrl);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnpExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnpExpireDate);
        return vnpParamsMap;
    }

}