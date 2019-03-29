package com.dame.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dame.gmall.bean.CartInfo;
import com.dame.gmall.bean.SkuInfo;
import com.dame.gmall.cart.service.CartCookieService;
import com.dame.gmall.service.ManageService;
import com.dame.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class CartCookieServiceImpl implements CartCookieService {

    // 定义购物车名称
    private static final String COOKIE_CART_NAME = "CART";
    // 设置cookie 过期时间
    private static final int COOKIE_CART_MAXAGE = 7 * 24 * 3600;

    @Reference
    private ManageService manageService;


    @Override
    public void addToCart(HttpServletRequest request, HttpServletResponse response, String skuId, Integer skuNum) {
        String cookieValue = CookieUtil.getCookieValue(request, COOKIE_CART_NAME, true);
        List<CartInfo> cartInfoList = new ArrayList<>();
        boolean flag = true;

        if (StringUtils.isNotEmpty(cookieValue)) {
            cartInfoList = JSON.parseArray(cookieValue, CartInfo.class);
            for (CartInfo cartInfo : cartInfoList) {
                if (skuId.equals(cartInfo.getSkuId())) {
                    cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
                    flag = false;
                    break;
                }
            }
        }
        // cookie中没有该商品
        if (flag) {
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            CartInfo cartInfo1 = new CartInfo();
            cartInfo1.setSkuId(skuId);
            cartInfo1.setCartPrice(skuInfo.getPrice());
            cartInfo1.setSkuPrice(skuInfo.getPrice());
            cartInfo1.setSkuName(skuInfo.getSkuName());
            cartInfo1.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo1.setSkuNum(skuNum);
            cartInfoList.add(cartInfo1);
        }

        // 保存到cookie中
        CookieUtil.setCookie(request, response, COOKIE_CART_NAME, JSON.toJSONString(cartInfoList), COOKIE_CART_MAXAGE, true);
    }


    @Override
    public List<CartInfo> getCartList(HttpServletRequest request) {
        String cookieValue = CookieUtil.getCookieValue(request, COOKIE_CART_NAME, true);
        if (StringUtils.isNotEmpty(cookieValue)) {
            List<CartInfo> cartInfoList = JSON.parseArray(cookieValue, CartInfo.class);
            return cartInfoList;
        }
        return null;
    }

    @Override
    public void deleteCartCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, COOKIE_CART_NAME);
    }

    @Override
    public void checkCart(HttpServletRequest request, HttpServletResponse response, String skuId, String isChecked) {
        String cookieValue = CookieUtil.getCookieValue(request, COOKIE_CART_NAME, true);
        List<CartInfo> cartInfoList = JSON.parseArray(cookieValue, CartInfo.class);
        for (CartInfo cartInfo : cartInfoList) {
            if (skuId.equals(cartInfo.getSkuId())) {
                cartInfo.setIsChecked(isChecked);
            }
        }
        CookieUtil.setCookie(request, response, COOKIE_CART_NAME, JSON.toJSONString(cartInfoList), COOKIE_CART_MAXAGE, true);
    }
}
