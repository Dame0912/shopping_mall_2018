package com.dame.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dame.gmall.bean.CartInfo;
import com.dame.gmall.bean.SkuInfo;
import com.dame.gmall.cart.service.CartCookieService;
import com.dame.gmall.config.LoginRequire;
import com.dame.gmall.service.CartInfoService;
import com.dame.gmall.service.ManageService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class CartController {

    @Reference
    private CartInfoService cartInfoService;

    @Reference
    private ManageService manageService;

    @Autowired
    private CartCookieService cartCookieService;


    /**
     * 添加购物车
     *
     * @param request
     * @return
     */
    @LoginRequire(autoRedirect = false)
    @RequestMapping("addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response) {
        String skuId = request.getParameter("skuId");
        String skuNum = request.getParameter("skuNum");
        String userId = (String) request.getAttribute("userId");

        // 登录状态
        if (StringUtils.isNotEmpty(userId)) {
            cartInfoService.addToCart(skuId, userId, Integer.parseInt(skuNum));
        }
        // 未登录状态
        else {
            cartCookieService.addToCart(request, response, skuId, Integer.parseInt(skuNum));
        }

        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        request.setAttribute("skuInfo", skuInfo);
        request.setAttribute("skuNum", skuNum);
        return "success";
    }

    /**
     * 购物车展示
     *
     * @return
     */
    @LoginRequire(autoRedirect = false)
    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request, HttpServletResponse response) {
        String userId = (String) request.getAttribute("userId");
        // 未登录，从cookie中获取数据
        if (StringUtils.isEmpty(userId)) {
            List<CartInfo> cartList = cartCookieService.getCartList(request);
            request.setAttribute("cartList", cartList);
        }
        // 登录
        else {
            // 从cookie中获取购物车，如果有需要合并，同时删除cookie中的购物车数据
            List<CartInfo> cartListFromCookie = cartCookieService.getCartList(request);
            List<CartInfo> cartList;
            if (CollectionUtils.isNotEmpty(cartListFromCookie)) {
                // 合并购物车
                cartList = cartInfoService.mergeToCartList(cartListFromCookie, userId);
                // 删除cookie中的购物车数据
                cartCookieService.deleteCartCookie(request, response);
            } else {
                // 从redis中取得，或者从数据库中
                cartList = cartInfoService.getCartList(userId);
            }
            request.setAttribute("cartList", cartList);
        }
        return "cartList";
    }

    /**
     * 购物车中，商品选中状态变更，由于该操作可能比较频繁，所以只更新redis或者cookie
     *
     * @param request
     * @param response
     */
    @LoginRequire(autoRedirect = false)
    @ResponseBody
    @RequestMapping("checkCart")
    public void checkCart(HttpServletRequest request, HttpServletResponse response) {
        String userId = (String) request.getAttribute("userId");
        String skuId = request.getParameter("skuId");
        String isChecked = request.getParameter("isChecked");
        if (StringUtils.isNotEmpty(userId)) {
            cartInfoService.checkCart(skuId, isChecked, userId);
        } else {
            cartCookieService.checkCart(request, response, skuId, isChecked);
        }
    }

    /**
     * 点击去结算，必须要求用户是登录状态。同时，如果cookie中有商品，需要合并
     *
     * @param request
     * @param response
     * @return
     */
    @LoginRequire(autoRedirect = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, HttpServletResponse response) {
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cookieHandlerCartList = cartCookieService.getCartList(request);
        if (CollectionUtils.isNotEmpty(cookieHandlerCartList)) {
            cartInfoService.mergeToCartList(cookieHandlerCartList, userId);
            cartCookieService.deleteCartCookie(request, response);
        }
        return "redirect://order.gmall.com/trade";
    }
}
