package com.dame.gmall.payment.service;

import com.dame.gmall.bean.PaymentInfo;

public interface PaymentService {

    /**
     * 保存支付信息入库
     * @param paymentInfo
     */
    public void savePaymentInfo(PaymentInfo paymentInfo);
}
