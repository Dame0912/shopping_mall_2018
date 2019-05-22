package com.dame.gmall.bean;

import com.dame.gmall.bean.enums.PaymentStatus;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付信息
 */
public class PaymentInfo implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 对外业务编号。订单Order中生成的对外交易编号。
     */
    @Column
    private String outTradeNo;

    /**
     * 订单ID
     */
    @Column
    private String orderId;

    /**
     * 支付宝交易编号。 初始为空，支付宝回调时存入
     */
    @Column
    private String alipayTradeNo;

    /**
     * 总金额
     */
    @Column
    private BigDecimal totalAmount;

    /**
     * 交易内容。利用商品名称拼接。
     */
    @Column
    private String subject;

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
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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
