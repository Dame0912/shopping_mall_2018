package com.dame.gmall.payment.service;

import com.dame.gmall.bean.PaymentInfo;

public interface PaymentService {

    /**
     * 保存支付信息入库
     * @param paymentInfo
     */
    public void savePaymentInfo(PaymentInfo paymentInfo);

    /**
     * 查询支付信息
     * @param paymentInfo
     * @return
     */
    public PaymentInfo getPaymentInfo(PaymentInfo paymentInfo);

    /**
     * 更新支付订单表
     * @param out_trade_no
     * @param paymentInfoUpd
     */
    public void updatePaymentInfo(String out_trade_no, PaymentInfo paymentInfoUpd);
}
