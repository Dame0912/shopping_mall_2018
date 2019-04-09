package com.dame.gmall.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车
 */
public class CartInfo implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private String id;

    /**
     * 用户id
     */
    @Column
    private String userId;

    /**
     * skuid
     */
    @Column
    private String skuId;

    /**
     * 放入购物车时价格（结账的时候的sku价格，可能和这个价格不同）
     */
    @Column
    private BigDecimal cartPrice;

    /**
     * 数量
     */
    @Column
    private Integer skuNum;

    /**
     * 图片路径
     */
    @Column
    private String imgUrl;

    /**
     * sku名称（冗余）
     */
    @Column
    private String skuName;

    /**
     * 实时价格
     */
    @Transient
    private BigDecimal skuPrice;

    /**
     * 下订单的时候，商品是否勾选
     */
    @Transient
    private String isChecked = "0";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public BigDecimal getCartPrice() {
        return cartPrice;
    }

    public void setCartPrice(BigDecimal cartPrice) {
        this.cartPrice = cartPrice;
    }

    public Integer getSkuNum() {
        return skuNum;
    }

    public void setSkuNum(Integer skuNum) {
        this.skuNum = skuNum;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public BigDecimal getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(BigDecimal skuPrice) {
        this.skuPrice = skuPrice;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }
}
