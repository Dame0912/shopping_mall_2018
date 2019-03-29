package com.dame.gmall.bean.enums;

/**
 * 支付状态
 */
public enum PaymentStatus {
    UNPAID("支付中"),
    PAID("已支付"),
    PAY_FAIL("支付失败"),
    ClOSED("已关闭");

    private final String comment;

    private PaymentStatus(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}
