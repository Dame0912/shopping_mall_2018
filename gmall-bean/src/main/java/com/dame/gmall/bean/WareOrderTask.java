package com.dame.gmall.bean;

import com.dame.gmall.bean.enums.TaskStatus;
import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 库存工作单
 *
 * @param
 * @return
 */
public class WareOrderTask {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 订单编号
     */
    @Column
    private String orderId;

    /**
     * 收货人
     */
    @Column
    private String consignee;

    /**
     * 收货人电话
     */
    @Column
    private String consigneeTel;

    /**
     * 送货地址
     */
    @Column
    private String deliveryAddress;

    /**
     * 订单备注
     */
    @Column
    private String orderComment;

    /**
     * 付款方式，1:在线付款 2:货到付款
     */
    @Column
    private String paymentWay;

    /**
     * 工作单状态。已付款；已减库存；已付款,库存超卖；已出库；已拆分
     */
    @Column
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private TaskStatus taskStatus;

    /**
     * 订单描述
     */
    @Column
    private String orderBody;

    /**
     * 物流单号
     */
    @Column
    private String trackingNo;

    /**
     * 创建时间
     */
    @Column
    private Date createTime;

    /**
     * 仓库编号
     */
    @Column
    private String wareId;

    /**
     * 工作单备注
     */
    @Column
    private String taskComment;

    /**
     * 库存工作单明细
     */
    @Transient
    private List<WareOrderTaskDetail> details;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getOrderComment() {
        return orderComment;
    }

    public void setOrderComment(String orderComment) {
        this.orderComment = orderComment;
    }

    public String getPaymentWay() {
        return paymentWay;
    }

    public void setPaymentWay(String paymentWay) {
        this.paymentWay = paymentWay;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getOrderBody() {
        return orderBody;
    }

    public void setOrderBody(String orderBody) {
        this.orderBody = orderBody;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<WareOrderTaskDetail> getDetails() {
        return details;
    }


    public void setDetails(List<WareOrderTaskDetail> details) {
        this.details = details;
    }

    public String getWareId() {
        return wareId;
    }

    public void setWareId(String wareId) {
        this.wareId = wareId;
    }

    public String getTaskComment() {
        return taskComment;
    }

    public void setTaskComment(String taskComment) {
        this.taskComment = taskComment;
    }
}
