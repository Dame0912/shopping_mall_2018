package com.dame.gmall.cart.service;

import com.dame.gmall.bean.CartInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *  说明：实际，用户未登录状态下，京东是将购物车数据保存在redis中。
 *  1、cookie禁用：使用浏览器的 local storage；但是ie浏览器没有该功能。
 *      如果一定要ie可以，那么只能使用url后面拼装参数的方式了
 */
public interface CartCookieService {

    /**
     * 用户未登录添加购物车，保存到cookie中
     * @param request
     * @param response
     * @param skuId
     * @param skuNum
     */
    public void addToCart(HttpServletRequest request, HttpServletResponse response, String skuId, Integer skuNum);

    /**
     * 用户未登录，从cookie中，获取数据
     * @param request
     * @return
     */
    public List<CartInfo> getCartList(HttpServletRequest request);

    /**
     * 删除cookie中的购物车数据
     * @param request
     * @param response
     */
    public void deleteCartCookie(HttpServletRequest request, HttpServletResponse response);

    /**
     * 购物车中商品选中状态变更
     * @param request
     * @param response
     * @param skuId
     * @param isChecked
     */
    public void checkCart(HttpServletRequest request, HttpServletResponse response, String skuId, String isChecked);
}
