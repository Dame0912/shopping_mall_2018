package com.dame.gmall.payment.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.dame.gmall.bean.PaymentInfo;
import com.dame.gmall.bean.enums.PaymentStatus;
import com.dame.gmall.config.ActiveMQUtil;
import com.dame.gmall.payment.mapper.PaymentInfoMapper;
import com.dame.gmall.payment.service.PaymentService;
import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Autowired
    private AlipayClient alipayClient;

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        // 根据订单id查询信息
        PaymentInfo paymentInfoQuery = new PaymentInfo();
        paymentInfoQuery.setOrderId(paymentInfo.getOrderId());
        List<PaymentInfo> list = paymentInfoMapper.select(paymentInfoQuery);
        if (list.size() > 0) {
            return;
        }
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public PaymentInfo getPaymentInfo(PaymentInfo paymentInfo) {
        return paymentInfoMapper.selectOne(paymentInfo);
    }

    @Override
    public void updatePaymentInfo(String out_trade_no, PaymentInfo paymentInfoUpd) {
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("out_trade_no", out_trade_no);
        paymentInfoMapper.updateByExampleSelective(paymentInfoUpd, example);
    }

    @Override
    public void sendPaymentResult(PaymentInfo paymentInfo, String result) {
        Connection connection = activeMQUtil.getConnection();
        try {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("PAYMENT_RESULT_QUEUE");
            MessageProducer producer = session.createProducer(queue);
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("orderId", paymentInfo.getOrderId());
            mapMessage.setString("result", result);
            producer.send(mapMessage);
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * 利用mq，发送延迟队列，查询支付宝，支付结果
     *
     * @param outTradeNo 单号
     * @param delaySec   延迟多少秒
     * @param checkCount 查询次数
     */
    @Override
    public void sendDelayPaymentResult(String outTradeNo, int delaySec, int checkCount) {
        Connection connection = activeMQUtil.getConnection();
        try {
            connection.start();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("PAYMENT_RESULT_CHECK_QUEUE");
            MessageProducer producer = session.createProducer(queue);
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("outTradeNo", outTradeNo);
            mapMessage.setInt("delaySec", delaySec);
            mapMessage.setInt("checkCount", checkCount);
            // 设置延迟多长时间
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delaySec * 1000);
            producer.send(mapMessage);
            session.commit();
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询支付结果，查看支付结果
     *
     * @param paymentInfoQuery
     * @return
     */
    @Override
    public boolean checkPayment(PaymentInfo paymentInfoQuery) {
        // 查询当前的支付信息
        PaymentInfo paymentInfo = getPaymentInfo(paymentInfoQuery);
        if (paymentInfo.getPaymentStatus() == PaymentStatus.PAID || paymentInfo.getPaymentStatus() == PaymentStatus.ClOSED) {
            return true;
        }

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + paymentInfo.getOutTradeNo() + "\"" +
                "  }");
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            if ("TRADE_SUCCESS".equals(response.getTradeStatus()) || "TRADE_FINISHED".equals(response.getTradeStatus())) {
                //  IPAD
                System.out.println("支付成功");
                // 修改支付状态
                PaymentInfo paymentInfoUpd = new PaymentInfo();
                paymentInfoUpd.setPaymentStatus(PaymentStatus.PAID);
                updatePaymentInfo(paymentInfo.getOutTradeNo(), paymentInfoUpd);
                // 发送mq支付结果，通知订单系统
                sendPaymentResult(paymentInfo, "success");
                return true;
            } else {
                System.out.println("支付失败");
                return false;
            }
        } else {
            System.out.println("支付失败");
            return false;
        }
    }

}
