package com.dame.gmall.service;

import com.dame.gmall.bean.OrderInfo;
import com.dame.gmall.bean.enums.ProcessStatus;

import java.util.List;
import java.util.Map;

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
     *
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
     *
     * @param orderId
     * @return
     */
    public OrderInfo getOrderInfo(String orderId);

    /**
     * 更新订单状态
     *
     * @param orderId
     * @param processStatus
     */
    public void updateOrderStatus(String orderId, ProcessStatus processStatus);

    /**
     * 通知库存系统减库存，利用activemq
     *
     * @param orderId
     */
    public void sendOrderStatus(String orderId);

    /**
     * 查询过期订单。依据订单过期时间和订单状态为未支付状态。
     *
     * @return
     */
    public List<OrderInfo> getExpiredOrderList();

    /**
     * 处理未支付的超期订单
     *
     * @param orderInfo
     */
    public void execExpiredOrder(OrderInfo orderInfo);

    /**
     * 拆单接口，根据仓库的地址不同，进行的拆单
     *
     * @param orderId
     * @param wareSkuMap
     * @return
     */
    public List<OrderInfo> splitOrder(String orderId, String wareSkuMap);

    /**
     * 构建返回给库存系统的拆单结果
     *
     * @param orderInfo
     * @return
     */
    public Map initWareOrder(OrderInfo orderInfo);
}
