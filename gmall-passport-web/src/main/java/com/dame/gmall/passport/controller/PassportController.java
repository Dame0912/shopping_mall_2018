package com.dame.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dame.gmall.bean.UserInfo;
import com.dame.gmall.service.UserInfoService;
import com.dame.gmall.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
public class PassportController {

    @Reference
    private UserInfoService userInfoService;

    @Value("${jwt.token.key}")
    private String jwtKey;

    /**
     * 登录页面首页
     *
     * @param request
     * @return
     */
    @RequestMapping("index")
    public String index(HttpServletRequest request) {
        String originUrl = request.getParameter("originUrl");
        // 保存上
        request.setAttribute("originUrl", originUrl);
        return "index";
    }

    /**
     * 用户登录
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("login")
    public String login(UserInfo userInfo, HttpServletRequest request) {
        // 获取用户的ip
        String ip = request.getParameter("X-forwarded-for");
        // 验证用户信息
        UserInfo loginUser = userInfoService.login(userInfo);
        if (null != loginUser) {
            // 做token -- JWT
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", loginUser.getId());
            map.put("nickName", loginUser.getNickName());

            String token = JwtUtil.encode(jwtKey, map, ip);
            return token;
        } else {
            return "fail";
        }
    }
}
