package com.dame.gmall.bean;

import com.dame.gmall.bean.enums.PaymentStatus;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PaymentInfo implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 订单中已生成的对外交易编号。订单中获取
     */
    @Column
    private String outTradeNo;

    /**
     * 订单ID
     */
    @Column
    private String orderId;

    /**
     * 订单编号  初始为空，支付宝回调时生成
     */
    @Column
    private String alipayTradeNo;

    /**
     * 总金额
     */
    @Column
    private BigDecimal totalAmount;

    /**
     * 订单名称。利用商品名称拼接。
     */
    @Column
    private String Subject;

    /**
     * 支付状态。
     */
    @Column
    private PaymentStatus paymentStatus;

    /**
     * 创建时间，当前时间
     */
    @Column
    private Date createTime;

    /**
     * 回调时间，初始为空，支付宝异步回调时记录
     */
    @Column
    private Date callbackTime;

    /**
     * 回调信息，初始为空，支付宝异步回调时记录
     */
    @Column
    private String callbackContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAlipayTradeNo() {
        return alipayTradeNo;
    }

    public void setAlipayTradeNo(String alipayTradeNo) {
        this.alipayTradeNo = alipayTradeNo;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCallbackTime() {
        return callbackTime;
    }

    public void setCallbackTime(Date callbackTime) {
        this.callbackTime = callbackTime;
    }

    public String getCallbackContent() {
        return callbackContent;
    }

    public void setCallbackContent(String callbackContent) {
        this.callbackContent = callbackContent;
    }
}
