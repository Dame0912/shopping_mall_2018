package com.dame.gmall.service;

import com.dame.gmall.bean.CartInfo;

import java.util.List;

public interface CartInfoService {

    /**
     * 登录状态下，保存购物车
     */
    public void addToCart(String skuId, String userId, Integer skuNum);

    /**
     * 登录状态下，查询购物车集合列表
     *
     * @param userId
     * @return
     */
    public List<CartInfo> getCartList(String userId);

    /**
     * 登录状态下，缓存中没有数据，则从数据库中加载，需要将商品最新的价格替换掉购物中的数据
     *
     * @param userId
     * @return
     */
    public List<CartInfo> loadCartDB(String userId);

    /**
     * 合并cookie中的购物车数据到db和redis中
     *
     * @param cartListFromCookie
     * @param userId
     * @return
     */
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListFromCookie, String userId);

    /**
     * 购物车中商品选中状态变更
     *
     * @param skuId
     * @param isChecked
     * @param userId
     */
    public void checkCart(String skuId, String isChecked, String userId);

    /**
     * 获取选中的购物车列表
     * @param userId
     * @return
     */
    public List<CartInfo> getCartCheckedList(String userId);
}
