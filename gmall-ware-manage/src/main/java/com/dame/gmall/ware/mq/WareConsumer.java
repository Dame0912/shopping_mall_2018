package com.dame.gmall.ware.mq;

import com.alibaba.fastjson.JSON;
import com.dame.gmall.bean.WareOrderTask;
import com.dame.gmall.bean.enums.TaskStatus;
import com.dame.gmall.ware.service.GwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

/**
 * MQ 监听器
 *
 * @param
 * @return
 */
@Component
public class WareConsumer {

    @Autowired
    private GwareService gwareService;

    /**
     * 监听订单系统，发送的支付成功订单
     *
     * @param textMessage
     * @throws JMSException
     */
    @JmsListener(destination = "ORDER_RESULT_QUEUE", containerFactory = "jmsQueueListener")
    public void receiveOrder(TextMessage textMessage) throws JMSException {
        String orderTaskJson = textMessage.getText();
        WareOrderTask wareOrderTask = JSON.parseObject(orderTaskJson, WareOrderTask.class);
        wareOrderTask.setTaskStatus(TaskStatus.PAID);
        // 保存该订单及明细
        gwareService.saveWareOrderTask(wareOrderTask);
        textMessage.acknowledge();

        // 拆单
        List<WareOrderTask> wareSubOrderTaskList = gwareService.checkOrderSplit(wareOrderTask);

        // 锁库存，通知订单系统锁库存结果
        if (wareSubOrderTaskList != null && wareSubOrderTaskList.size() >= 2) {
            for (WareOrderTask orderTask : wareSubOrderTaskList) {
                gwareService.lockStock(orderTask);
            }
        } else {
            gwareService.lockStock(wareOrderTask);
        }
    }
}
