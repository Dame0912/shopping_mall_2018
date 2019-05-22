package com.dame.gmall.order.mq;

import com.dame.gmall.bean.enums.ProcessStatus;
import com.dame.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

/**
 * activemq消费类
 */
@Component
public class OrderConsumer {

    @Autowired
    private OrderService orderService;

    /**
     * 支付系统支付结果的监听器
     */
    @JmsListener(destination = "PAYMENT_RESULT_QUEUE", containerFactory = "jmsQueueListenerContainerFactory")
    public void consumerPaymentResult(Message message) throws JMSException {
        MapMessage mapMessage = (MapMessage) message;
        String orderId = mapMessage.getString("orderId");
        String result = mapMessage.getString("result");
        if ("success".equals(result)) {
            // 更新订单状态
            orderService.updateOrderStatus(orderId, ProcessStatus.PAID);
            // 通知库存系统减库存
            orderService.sendOrderStatus(orderId);
            // 更新订单状态
            orderService.updateOrderStatus(orderId, ProcessStatus.NOTIFIED_WARE);
        } else {
            // 更新订单状态
            orderService.updateOrderStatus(orderId, ProcessStatus.UNPAID);
        }
        message.acknowledge();
    }

    /**
     * 库存系统减库存结果的监听器
     */
    @JmsListener(destination = "SKU_DEDUCT_QUEUE", containerFactory = "jmsQueueListenerContainerFactory")
    public void consumeSkuDeduct(MapMessage mapMessage) throws JMSException {
        String orderId = mapMessage.getString("orderId");
        String status = mapMessage.getString("status");
        if ("DEDUCTED".equals(status)) { // 库存系统减库存成功
            orderService.updateOrderStatus(orderId, ProcessStatus.WAITING_DELEVER);
        } else { // 库存系统减库存失败
            orderService.updateOrderStatus(orderId, ProcessStatus.STOCK_EXCEPTION);
        }
    }

}
