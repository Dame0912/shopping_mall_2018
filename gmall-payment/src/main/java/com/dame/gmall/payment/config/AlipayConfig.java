package com.dame.gmall.payment.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:alipay.properties")
public class AlipayConfig {

    @Value("${alipay_url}")
    private String alipay_url;

    @Value("${app_id}")
    private String app_id;

    @Value("${app_private_key}")
    private String app_private_key;

    public static final String format = "json";
    public static final String charset = "utf-8";
    public static final String signType = "RSA2";

    public static String alipay_public_key;

    public static String return_payment_url;

    public static String notify_payment_url;

    public static String return_order_url;

    @Value("${alipay_public_key}")
    public void setAlipay_public_key(String alipay_public_key) {
        AlipayConfig.alipay_public_key = alipay_public_key;
    }

    @Value("${return_payment_url}")
    public void setReturn_url(String return_payment_url) {
        AlipayConfig.return_payment_url = return_payment_url;
    }

    @Value("${notify_payment_url}")
    public void setNotify_url(String notify_payment_url) {
        AlipayConfig.notify_payment_url = notify_payment_url;
    }

    @Value("${return_order_url}")
    public void setReturn_order_url(String return_order_url) {
        AlipayConfig.return_order_url = return_order_url;
    }

    @Bean
    public AlipayClient alipayClient() {
        AlipayClient alipayClient = new DefaultAlipayClient(alipay_url, app_id, app_private_key, format, charset, alipay_public_key, signType);
        return alipayClient;
    }

}
