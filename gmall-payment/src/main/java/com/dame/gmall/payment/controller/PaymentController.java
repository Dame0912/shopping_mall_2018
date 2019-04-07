package com.dame.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.dame.gmall.bean.OrderInfo;
import com.dame.gmall.bean.PaymentInfo;
import com.dame.gmall.bean.enums.PaymentStatus;
import com.dame.gmall.config.LoginRequire;
import com.dame.gmall.payment.config.AlipayConfig;
import com.dame.gmall.payment.service.PaymentService;
import com.dame.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {

    @Reference
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AlipayClient alipayClient;


    @RequestMapping("index")
    @LoginRequire
    public String index(HttpServletRequest request, Model model) {
        // 获取订单的id
        String orderId = request.getParameter("orderId");
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);
        model.addAttribute("orderId", orderId);
        model.addAttribute("totalAmount", orderInfo.getTotalAmount());
        return "paymentIndex";
    }

    /**
     * 请求支付宝支付
     *
     * @param request
     * @param response
     * @return
     */
    @LoginRequire
    @ResponseBody
    @RequestMapping(value = "alipay/submit", method = RequestMethod.POST)
    public String submitPayment(HttpServletRequest request, HttpServletResponse response) {
        // 获取订单Id
        String orderId = request.getParameter("orderId");
        // 取得订单信息
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);
        // 保存支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderId(orderId);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        paymentInfo.setSubject(orderInfo.getTradeBody());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        paymentInfo.setCreateTime(new Date());
        paymentService.savePaymentInfo(paymentInfo);

        // 支付宝参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址

        // 声明一个Map
        Map<String, Object> bizContnetMap = new HashMap<>();
        bizContnetMap.put("out_trade_no", paymentInfo.getOutTradeNo());
        bizContnetMap.put("product_code", "FAST_INSTANT_TRADE_PAY");
        bizContnetMap.put("subject", paymentInfo.getSubject());
        bizContnetMap.put("total_amount", paymentInfo.getTotalAmount());
        // 将map变成json
        String Json = JSON.toJSONString(bizContnetMap);
        alipayRequest.setBizContent(Json);
        String form = "";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");
        // 发送延迟队列，主要查询支付宝，支付结果
        paymentService.sendDelayPaymentResult(paymentInfo.getOutTradeNo(), 15, 3);
        return form;
    }

    /**
     * 支付宝支付完成，同步回调地址
     *
     * @return
     */
    @RequestMapping(value = "alipay/callback/return", method = RequestMethod.GET)
    public String callbackReturn() {
        return "redirect://" + AlipayConfig.return_order_url;
    }

    /**
     * 支付宝支付完成，异步回调处理
     *
     * @param paramMap
     * @param request
     * @return
     * @throws AlipayApiException
     */
    @RequestMapping(value = "/alipay/callback/notify", method = RequestMethod.POST)
    @ResponseBody
    public String paymentNotify(@RequestParam Map<String, String> paramMap, HttpServletRequest request) throws AlipayApiException {
        // 验签
        boolean flag = AlipaySignature.rsaCheckV1(paramMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.signType);
        if (!flag) {
            return "fail";
        }
        // 判断结束
        String trade_status = paramMap.get("trade_status");
        if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {//支付成功  交易完结
            // 查单据是否处理
            String out_trade_no = paramMap.get("out_trade_no");
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOutTradeNo(out_trade_no);
            PaymentInfo paymentInfoHas = paymentService.getPaymentInfo(paymentInfo);

            if (paymentInfoHas.getPaymentStatus() == PaymentStatus.PAID || paymentInfoHas.getPaymentStatus() == PaymentStatus.ClOSED) {
                return "fail";
            } else {
                // 修改
                PaymentInfo paymentInfoUpd = new PaymentInfo();
                // 设置状态
                paymentInfoUpd.setPaymentStatus(PaymentStatus.PAID);
                // 设置回调时间
                paymentInfoUpd.setCallbackTime(new Date());
                // 设置内容
                paymentInfoUpd.setCallbackContent(paramMap.toString());
                paymentService.updatePaymentInfo(out_trade_no, paymentInfoUpd);
                // 利用activemq发送给订单系统
                paymentService.sendPaymentResult(paymentInfo, "success");
                return "success";
            }
        }
        return "fail";
    }

}
