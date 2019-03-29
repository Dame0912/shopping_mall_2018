package com.dame.gmall.bean.enums;

/**
 * 支付方式
 */
public enum PaymentWay {
    ONLINE("在线支付"),
    OUTLINE("货到付款");

    private final String comment;

    private PaymentWay(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}
