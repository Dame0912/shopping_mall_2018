package com.dame.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dame.gmall.bean.*;
import com.dame.gmall.bean.enums.OrderStatus;
import com.dame.gmall.bean.enums.ProcessStatus;
import com.dame.gmall.config.LoginRequire;
import com.dame.gmall.service.CartInfoService;
import com.dame.gmall.service.ManageService;
import com.dame.gmall.service.OrderService;
import com.dame.gmall.service.UserInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Reference
    private UserInfoService userInfoService;

    @Reference
    private CartInfoService cartInfoService;

    @Reference
    private OrderService orderService;

    @Reference
    private ManageService manageService;

    /**
     * 订单内容展示
     *
     * @param request
     * @return
     */
    @LoginRequire
    @RequestMapping("trade")
    public String tradeInit(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        // 得到选中的购物车列表
        List<CartInfo> cartCheckedList = cartInfoService.getCartCheckedList(userId);
        // 收货人地址
        List<UserAddress> userAddressList = userInfoService.getUserAddressList(userId);
        request.setAttribute("userAddressList", userAddressList);
        // 订单信息集合
        List<OrderDetail> orderDetailList = new ArrayList<>(cartCheckedList.size());
        for (CartInfo cartInfo : cartCheckedList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetailList.add(orderDetail);
        }
        request.setAttribute("orderDetailList", orderDetailList);

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(orderDetailList);
        orderInfo.sumTotalAmount();
        request.setAttribute("totalAmount", orderInfo.getTotalAmount());

        // 获取tradeNo号
        String tradeNo = orderService.getTradeNo(userId);
        request.setAttribute("tradeNo", tradeNo);
        return "trade";
    }


    /**
     * 提交订单，准备支付
     *
     * @param orderInfo
     * @param request
     * @return
     */
    @LoginRequire
    @RequestMapping(value = "submitOrder", method = RequestMethod.POST)
    public String submitOrder(OrderInfo orderInfo, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");

        // 判断表单是否重复提交
        boolean tradeNo = orderService.checkTradeCode(userId, request.getParameter("tradeNo"));
        if (!tradeNo) {
            request.setAttribute("errMsg", "该页面已失效，请重新结算!");
            return "tradeFail";
        }

        // 验价格，验库存
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            // 验价格
            SkuInfo skuInfo = manageService.getSkuInfo(orderDetail.getSkuId());
            if (null == skuInfo || (orderDetail.getOrderPrice().compareTo(skuInfo.getPrice())) != 0) {
                request.setAttribute("errMsg", "商品价格已改变，请重新下单。");
                return "tradeFail";
            }
            // 调用库存系统，验库存
            boolean checkStock = orderService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
            if (!checkStock) {
                request.setAttribute("errMsg", "库存数量不足，请重新下单。");
                return "tradeFail";
            }
        }

        // 初始化参数
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        orderInfo.sumTotalAmount();
        orderInfo.setUserId(userId);
        // 保存
        String orderId = orderService.saveOrder(orderInfo);
        // 删除tradeNo，防止重复提交
        orderService.delTradeCode(userId);
        return "redirect://payment.gmall.com/index?orderId=" + orderId;
    }

    /**
     * 库存系统调用
     * 拆单接口，根据仓库的地址不同，进行的拆单
     *
     * @param request
     * @return
     */
    @RequestMapping("orderSplit")
    @ResponseBody
    public String orderSplit(HttpServletRequest request) {
        String orderId = request.getParameter("orderId");
        String wareSkuMap = request.getParameter("wareSkuMap");
        // 定义订单集合
        List<OrderInfo> subOrderInfoList = orderService.splitOrder(orderId, wareSkuMap);
        List<Map> wareMapList = new ArrayList<>();
        for (OrderInfo orderInfo : subOrderInfoList) {
            Map map = orderService.initWareOrder(orderInfo);
            wareMapList.add(map);
        }
        return JSON.toJSONString(wareMapList);
    }

}
