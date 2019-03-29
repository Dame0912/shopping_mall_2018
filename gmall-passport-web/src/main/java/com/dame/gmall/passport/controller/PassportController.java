package com.dame.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dame.gmall.bean.UserInfo;
import com.dame.gmall.passport.util.JwtUtil;
import com.dame.gmall.service.UserInfoService;
import com.dame.gmall.util.CookieUtil;
import com.dame.gmall.util.URLUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        if(StringUtils.isEmpty(originUrl)){
            originUrl = request.getHeader("referer");
        }

        // 保存上，方便登录完成后跳转
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
        String ip = request.getHeader("X-forwarded-for");
        // 验证用户信息
        UserInfo loginUser = userInfoService.login(userInfo);
        if (null != loginUser) {
            // token的playhold中的内容
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", loginUser.getId());
            map.put("nickName", loginUser.getNickName());

            // 做token -- JWT
            String token = JwtUtil.encode(jwtKey, map, ip);
            return token;
        } else {
            return "fail";
        }
    }

    /**
     * 身份认证
     *
     * @param request
     * @return
     */
    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request) {
        // 获取用户的ip
        String currentIp = request.getParameter("currentIp");
        String token = request.getParameter("passToken");
        // jwt身份验证，同时获取保存的消息体
        Map<String, Object> playHoldMap = JwtUtil.decode(token, jwtKey, currentIp);
        if (MapUtils.isNotEmpty(playHoldMap)) {
            // 检查redis中是否有用户信息
            String userId = (String) playHoldMap.get("userId");
            UserInfo userInfo = userInfoService.verify(userId);
            if (null != userInfo) {
                return "success";
            }
        }
        return "fail";
    }

    /**
     * 退出登录
     *
     * @return
     */
    @RequestMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String referer = request.getHeader("referer");
        String ip = request.getHeader("X-forwarded-for");
        String passToken = CookieUtil.getCookieValue(request, "passToken", false);
        if (StringUtils.isEmpty(passToken)) {
            passToken = URLUtil.getParam(referer, "newToken");
            referer = URLUtil.moveParam(referer, "newToken");
        }
        if(StringUtils.isNotEmpty(passToken)){
            Map<String, Object> playHoldMap = JwtUtil.decode(passToken, jwtKey, ip);
            if (MapUtils.isNotEmpty(playHoldMap)) {
                String userId = (String) playHoldMap.get("userId");
                userInfoService.logout(userId);
            }
        }
        // 清空cookie
        CookieUtil.setCookie(request, response, "passToken", null, 0, false);
        try {
            // 重定向到原来的页面
            response.sendRedirect(referer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
