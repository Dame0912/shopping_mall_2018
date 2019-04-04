package com.dame.gmall.bean;

import com.dame.gmall.bean.enums.OrderStatus;
import com.dame.gmall.bean.enums.PaymentWay;
import com.dame.gmall.bean.enums.ProcessStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单实体
 */
public class OrderInfo implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 用户Id
     */
    @Column
    private String userId;

    /**
     * 收货人名称
     */
    @Column
    private String consignee;

    /**
     * 收货人电话
     */
    @Column
    private String consigneeTel;

    /**
     * 收货地址
     */
    @Column
    private String deliveryAddress;

    /**
     * 总金额
     */
    @Column
    private BigDecimal totalAmount;

    /**
     * 订单状态，用于显示给用户查看。
     */
    @Column
    private OrderStatus orderStatus;

    /**
     * 订单进度状态，程序控制、 后台管理查看
     */
    @Column
    private ProcessStatus processStatus;

    /**
     * 订单备注，用户在页面输入的信息
     */
    @Column
    private String orderComment;

    /**
     * 支付方式（网上支付、货到付款）
     */
    @Column
    private PaymentWay paymentWay;

    /**
     * 订单创建时间
     */
    @Column
    private Date createTime;

    /**
     * 订单过期时间。默认当前时间+1天
     */
    @Column
    private Date expireTime;

    /**
     * 拆单时产生，默认为空。
     */
    @Column
    private String parentOrderId;

    /**
     * 物流编号,初始为空，发货后补充
     */
    @Column
    private String trackingNo;

    /**
     * 第三方支付编号，自己生成，唯一，保证支付的幂等性
     */
    @Column
    private String outTradeNo;

    @Transient
    private List<OrderDetail> orderDetailList;

    @Transient
    private String wareId;

    /**
     * 计算总金额
     */
    public void sumTotalAmount() {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OrderDetail orderDetail : orderDetailList) {
            totalAmount = totalAmount.add(orderDetail.getOrderPrice().multiply(new BigDecimal(orderDetail.getSkuNum())));
        }
        this.totalAmount = totalAmount;
    }

    /**
     * 生成订单名称，对应 paymentInfo的 Subject字段
     * @return
     */
    public String getTradeBody() {
        OrderDetail orderDetail = orderDetailList.get(0);
        String tradeBody = orderDetail.getSkuName() + "等" + orderDetailList.size() + "件商品";
        return tradeBody;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getConsigneeTel() {
        return consigneeTel;
    }

    public void setConsigneeTel(String consigneeTel) {
        this.consigneeTel = consigneeTel;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    public String getOrderComment() {
        return orderComment;
    }

    public void setOrderComment(String orderComment) {
        this.orderComment = orderComment;
    }

    public PaymentWay getPaymentWay() {
        return paymentWay;
    }

    public void setPaymentWay(PaymentWay paymentWay) {
        this.paymentWay = paymentWay;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(String parentOrderId) {
        this.parentOrderId = parentOrderId;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public List<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    public String getWareId() {
        return wareId;
    }

    public void setWareId(String wareId) {
        this.wareId = wareId;
    }
}
