package com.dame.gmall.payment.service.impl;

import com.dame.gmall.bean.PaymentInfo;
import com.dame.gmall.payment.mapper.PaymentInfoMapper;
import com.dame.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

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
}
