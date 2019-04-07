package com.dame.gmall.service;

import com.dame.gmall.bean.OrderInfo;
import com.dame.gmall.bean.enums.OrderStatus;
import com.dame.gmall.bean.enums.ProcessStatus;

public interface OrderService {

    /**
     * 生成结算流水号，为了防止提交支付表单重复提交
     *
     * @param userId
     * @return
     */
    public String getTradeNo(String userId);

    /**
     * 检查结算流水号，看订单是不是重复提交
     *
     * @param userId
     * @param tradeCodeNo
     * @return
     */
    public boolean checkTradeCode(String userId, String tradeCodeNo);

    /**
     * 删除结算流水号
     *
     * @param userId
     */
    public void delTradeCode(String userId);

    /**
     * 验证库存
     * @param skuId
     * @param skuNum
     * @return
     */
    boolean checkStock(String skuId, Integer skuNum);

    /**
     * 返回orderId，保存完，应该调到支付，根据orderId。
     *
     * @param orderInfo
     * @return
     */
    public String saveOrder(OrderInfo orderInfo);

    /**
     * 根据 orderId 获取 OrderInfo
     * @param orderId
     * @return
     */
    public OrderInfo getOrderInfo(String orderId);

    /**
     * 更新订单状态
     * @param orderId
     * @param processStatus
     */
    public void updateOrderStatus(String orderId, ProcessStatus processStatus);

    /**
     * 通知库存系统减库存，利用activemq
     * @param orderId
     */
    public void sendOrderStatus(String orderId);
}
