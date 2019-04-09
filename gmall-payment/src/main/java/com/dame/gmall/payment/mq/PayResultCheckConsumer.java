package com.dame.gmall.payment.mq;

import com.dame.gmall.bean.PaymentInfo;
import com.dame.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

/**
 * 支付宝，支付结果，主动查询的mq监听器
 */
@Component
public class PayResultCheckConsumer {

    @Autowired
    private PaymentService paymentService;

    @JmsListener(destination = "PAYMENT_RESULT_CHECK_QUEUE", containerFactory = "jmsQueueListenerContainerFactory")
    public void consumerPaymentResultCheck(Message message) throws JMSException {
        // 获取数据
        MapMessage mapMessage = (MapMessage) message;
        String outTradeNo = mapMessage.getString("outTradeNo");
        int delaySec = mapMessage.getInt("delaySec");
        int checkCount = mapMessage.getInt("checkCount");
        // 检查
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(outTradeNo);
        boolean flag = paymentService.checkPayment(paymentInfo);
        // 支付不成功，再次发送到队列中
        if (!flag && checkCount != 0) {
            paymentService.sendDelayPaymentResult(outTradeNo, delaySec, checkCount - 1);
        }
    }
}
