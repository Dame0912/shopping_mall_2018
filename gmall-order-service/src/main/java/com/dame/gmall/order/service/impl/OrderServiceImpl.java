package com.dame.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dame.gmall.bean.OrderDetail;
import com.dame.gmall.bean.OrderInfo;
import com.dame.gmall.config.RedisUtil;
import com.dame.gmall.order.mapper.OrderDetailMapper;
import com.dame.gmall.order.mapper.OrderInfoMapper;
import com.dame.gmall.order.util.HttpClientUtil;
import com.dame.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 生成结算流水号，防止表单重复提交
     *
     * @param userId
     * @return
     */
    @Override
    public String getTradeNo(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String tradeNoKey = "user:" + userId + ":tradeCode";
        String tradeCode = UUID.randomUUID().toString();
        jedis.setex(tradeNoKey, 10 * 60, tradeCode);
        jedis.close();
        return tradeCode;
    }

    /**
     * 检查流水号，看订单是不是重复提交
     *
     * @param userId
     * @param tradeCodeNo
     * @return
     */
    @Override
    public boolean checkTradeCode(String userId, String tradeCodeNo) {
        Jedis jedis = redisUtil.getJedis();
        String tradeNoKey = "user:" + userId + ":tradeCode";
        String tradeCode = jedis.get(tradeNoKey);
        jedis.close();
        if (tradeCode.equals(tradeCode)) {
            return true;
        }
        return false;
    }

    /**
     * 删除结算流水号，防止表单重复提交
     *
     * @param userId
     */
    @Override
    public void delTradeCode(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String tradeNoKey = "user:" + userId + ":tradeCode";
        jedis.del(tradeNoKey);
        jedis.close();
    }

    /**
     * 验库存
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @Override
    public boolean checkStock(String skuId, Integer skuNum) {
        String result = HttpClientUtil.doGet("http://www.gware.com/hasStock?skuId=" + skuId + "&num=" + skuNum);
        if ("1".equals(result)) {
            return true;
        }
        return false;
    }

    /**
     * 保存订单
     *
     * @param orderInfo
     * @return 订单id，供支付使用
     */
    @Override
    public String saveOrder(OrderInfo orderInfo) {
        // 设置创建时间
        orderInfo.setCreateTime(new Date());
        // 设置失效时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        orderInfo.setExpireTime(calendar.getTime());
        // 生成第三方支付编号，唯一，保证一个订单只能支付一次，保证支付的幂等性
        String outTradeNo = "DAME" + System.currentTimeMillis() + "" + new Random().nextInt(1000);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfoMapper.insertSelective(orderInfo);

        // 插入订单详情表
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insertSelective(orderDetail);
        }

        // 为了跳转到支付页面使用。支付会根据订单id进行支付。
        return orderInfo.getId();
    }

    /**
     * 根据orderId获取OrderInfo
     * @param orderId
     * @return
     */
    @Override
    public OrderInfo getOrderInfo(String orderId) {
        // 根据orderId 查询orderDetail
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);
        // 查询orderInfo
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(orderId);
        orderInfo.setOrderDetailList(orderDetails);
        return orderInfo;
    }
}
