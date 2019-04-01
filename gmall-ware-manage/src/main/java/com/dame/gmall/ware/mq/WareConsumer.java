package com.dame.gmall.ware.mq;

import com.alibaba.fastjson.JSON;
import com.dame.gmall.bean.WareOrderTask;
import com.dame.gmall.bean.enums.TaskStatus;
import com.dame.gmall.ware.config.ActiveMQUtil;
import com.dame.gmall.ware.mapper.WareOrderTaskDetailMapper;
import com.dame.gmall.ware.mapper.WareOrderTaskMapper;
import com.dame.gmall.ware.mapper.WareSkuMapper;
import com.dame.gmall.ware.service.GwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @param
 * @return
 */

public class WareConsumer {


    @Autowired
    WareOrderTaskMapper wareOrderTaskMapper;

    @Autowired
    WareOrderTaskDetailMapper wareOrderTaskDetailMapper;

    @Autowired
    WareSkuMapper wareSkuMapper;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Autowired
    JmsTemplate jmsTemplate;


    @Autowired
    GwareService gwareService;


    @JmsListener(destination = "ORDER_RESULT_QUEUE", containerFactory = "jmsQueueListener")
    public void receiveOrder(TextMessage textMessage) throws JMSException {
        String orderTaskJson = textMessage.getText();
        WareOrderTask wareOrderTask = JSON.parseObject(orderTaskJson, WareOrderTask.class);
        wareOrderTask.setTaskStatus(TaskStatus.PAID);
        gwareService.saveWareOrderTask(wareOrderTask);
        textMessage.acknowledge();


        List<WareOrderTask> wareSubOrderTaskList = gwareService.checkOrderSplit(wareOrderTask);
        if (wareSubOrderTaskList != null && wareSubOrderTaskList.size() >= 2) {
            for (WareOrderTask orderTask : wareSubOrderTaskList) {
                gwareService.lockStock(orderTask);
            }
        } else {
            gwareService.lockStock(wareOrderTask);
        }


    }


}
