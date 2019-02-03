package com.dame.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dame.gamll.bean.UserAddress;
import com.dame.gmall.service.UserInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    UserInfoService userInfoService;

    @RequestMapping("trade")
    @ResponseBody
    public List<UserAddress> trade(HttpServletRequest request){
        String userId = request.getParameter("userId");
        return userInfoService.getUserAddressList(userId);
    }
}
