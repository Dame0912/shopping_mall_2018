package com.dame.gmall.bean.enums;

/**
 * 库存系统的订单状态
 *
 * @param
 * @return
 */
public enum TaskStatus {
    PAID("已付款"),
    DEDUCTED("已减库存"),
    OUT_OF_STOCK("已付款，库存超卖"),
    DELEVERED("已出库"),
    SPLIT("已拆分");

    private final String comment;

    private TaskStatus(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

}
