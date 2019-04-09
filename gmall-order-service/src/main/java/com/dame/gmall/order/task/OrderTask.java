package com.dame.gmall.order.task;

import com.dame.gmall.bean.OrderInfo;
import com.dame.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@EnableScheduling
@Component
public class OrderTask {

    @Autowired
    private OrderService orderService;

    /**
     * 20秒，扫描未支付的超期订单
     */
    @Scheduled(cron = "0/20 * * * * ?")
    public void checkOrder() {
        List<OrderInfo> expiredOrderList = orderService.getExpiredOrderList();
        for (OrderInfo orderInfo : expiredOrderList) {
            // 处理未完成订单
            orderService.execExpiredOrder(orderInfo);
        }
    }

}
