package com.dame.gmall.payment.service;

import com.dame.gmall.bean.PaymentInfo;

public interface PaymentService {

    /**
     * 保存支付信息入库
     *
     * @param paymentInfo
     */
    public void savePaymentInfo(PaymentInfo paymentInfo);

    /**
     * 查询支付信息
     *
     * @param paymentInfo
     * @return
     */
    public PaymentInfo getPaymentInfo(PaymentInfo paymentInfo);

    /**
     * 更新支付订单表
     *
     * @param out_trade_no
     * @param paymentInfoUpd
     */
    public void updatePaymentInfo(String out_trade_no, PaymentInfo paymentInfoUpd);

    /**
     * 通过MQ发送支付结果
     *
     * @param paymentInfo
     * @param result
     */
    public void sendPaymentResult(PaymentInfo paymentInfo, String result);

    /**
     * 利用mq，发送延迟队列，查询支付宝，支付结果
     * @param outTradeNo 单号
     * @param delaySec  延迟多少秒
     * @param checkCount    查询次数
     */
    public void sendDelayPaymentResult(String outTradeNo, int delaySec, int checkCount);

    /**
     * 查询支付宝，支付结果
     * @param paymentInfoQuery
     * @return
     */
    public boolean checkPayment(PaymentInfo paymentInfoQuery);
}
