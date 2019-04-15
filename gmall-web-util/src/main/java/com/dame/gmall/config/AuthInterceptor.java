package com.dame.gmall.config;

import com.alibaba.fastjson.JSON;
import com.dame.gmall.common.WebConst;
import com.dame.gmall.util.CookieUtil;
import com.dame.gmall.util.HttpClientUtil;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 身份认证及存取cookie的拦截器
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 将token放入cookie中
        String passToken = request.getParameter("newToken");
        if (!StringUtils.isEmpty(passToken)) {
            CookieUtil.setCookie(request, response, "passToken", passToken, WebConst.COOKIE_MAXAGE, false);
        }
        if (StringUtils.isEmpty(passToken)) {
            //  如果用户登录了，访问其他页面的时候不会有newToken，那么token 可能已经在cookie 中存在了
            passToken = CookieUtil.getCookieValue(request, "passToken", false);
        }
        // 已经登录的token，获取其中的信息，此时，不需要关注是否认证。
        if (!StringUtils.isEmpty(passToken)) {
            // 读取token，获取其中的数据
            Map map = getUserMapByToken(passToken);
            String nickName = (String) map.get("nickName");
            request.setAttribute("nickName", nickName);
        }

        // 添加要验证的注解，请求passport验证登录信息是否准确
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginRequire loginRequire = handlerMethod.getMethodAnnotation(LoginRequire.class);
        if (null != loginRequire) {
            String ipAddr = request.getHeader("X-forwarded-for");
            // 请求认证中心认证
            String verify = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?passToken=" + passToken + "&currentIp=" + ipAddr);
            if ("success".equals(verify)) {
                Map map = getUserMapByToken(passToken);
                String userId = (String) map.get("userId");
                request.setAttribute("userId", userId);
                return true;
            } else {
                // 重定向到登录界面
                if (loginRequire.autoRedirect()) {
                    String requestURL = request.getRequestURL().toString();
                    String encodeURL = URLEncoder.encode(requestURL, "utf-8");
                    response.sendRedirect(WebConst.LOGIN_ADDRESS + "?originUrl=" + encodeURL);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * newToken=eyJhbGciOiJIUzI1NiJ9.eyJuaWNrTmFtZSI6IkFkbWluaXN0cmF0b3IiLCJ1c2VySWQiOiIyIn0.2OSlBiXpWs69wyWwKIFOQeGf_4ck8Uzfhd77WZb8XUg
     * token由3段组成，第一段是公共部分，第二段为私有部分，第三段为签名
     * 前两段也就是base64编码，如果不需要认证，只为了取出使用，可以直接解码使用。
     *
     * @param passToken
     * @return
     */
    private Map getUserMapByToken(String passToken) {
        String playHold = StringUtils.substringBetween(passToken, ".");
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        byte[] decode = base64UrlCodec.decode(playHold);
        String decodeStr = null;
        try {
            decodeStr = new String(decode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map map = JSON.parseObject(decodeStr, Map.class);
        return map;
    }
}
