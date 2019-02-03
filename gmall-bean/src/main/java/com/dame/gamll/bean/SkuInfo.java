package com.dame.gamll.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * sku信息
 */
public class SkuInfo implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private String id;

    @Column
    private String spuId;
    /**
     * 价格
     */
    @Column
    private BigDecimal price;
    /**
     * 名称
     */
    @Column
    private String skuName;
    /**
     * 重量
     */
    @Column
    private BigDecimal weight;
    /**
     * 描述
     */
    @Column
    private String skuDesc;
    /**
     * 三级分类Id
     */
    @Column
    private String catalog3Id;
    /**
     * 默认图片
     */
    @Column
    private String skuDefaultImg;
    /**
     * sku的图片列表
     */
    @Transient
    private List<SkuImage> skuImageList;
    /**
     * sku的平台属性列表
     */
    @Transient
    private List<SkuAttrValue> skuAttrValueList;
    /**
     * sku的销售属性列表
     */
    @Transient
    private List<SkuSaleAttrValue> skuSaleAttrValueList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpuId() {
        return spuId;
    }

    public void setSpuId(String spuId) {
        this.spuId = spuId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getSkuDesc() {
        return skuDesc;
    }

    public void setSkuDesc(String skuDesc) {
        this.skuDesc = skuDesc;
    }

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getSkuDefaultImg() {
        return skuDefaultImg;
    }

    public void setSkuDefaultImg(String skuDefaultImg) {
        this.skuDefaultImg = skuDefaultImg;
    }

    public List<SkuImage> getSkuImageList() {
        return skuImageList;
    }

    public void setSkuImageList(List<SkuImage> skuImageList) {
        this.skuImageList = skuImageList;
    }

    public List<SkuAttrValue> getSkuAttrValueList() {
        return skuAttrValueList;
    }

    public void setSkuAttrValueList(List<SkuAttrValue> skuAttrValueList) {
        this.skuAttrValueList = skuAttrValueList;
    }

    public List<SkuSaleAttrValue> getSkuSaleAttrValueList() {
        return skuSaleAttrValueList;
    }

    public void setSkuSaleAttrValueList(List<SkuSaleAttrValue> skuSaleAttrValueList) {
        this.skuSaleAttrValueList = skuSaleAttrValueList;
    }
}
