package com.dame.gmall.bean.enums;

/**
 * 订单状态，给用户展示的
 */
public enum OrderStatus {
    UNPAID("未支付"),
    PAID("已支付"),
    WAITING_DELEVER("待发货"),
    DELEVERED("已发货"),
    CLOSED("已关闭"),
    FINISHED("已完结"),
    SPLIT("订单已拆分");

    private final String comment;

    private OrderStatus(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

}
