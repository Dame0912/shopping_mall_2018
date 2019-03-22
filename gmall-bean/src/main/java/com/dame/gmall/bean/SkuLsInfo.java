package com.dame.gmall.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *  es中保存Sku的信息
 */
public class SkuLsInfo implements Serializable {
    private String id;
    private BigDecimal price;
    private String skuName;
    private String skuDesc;
    private String catalog3Id;
    private String skuDefaultImg;
    private Long hotScore = 0L;
    List<SkuLsAttrValue> skuAttrValueList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Long getHotScore() {
        return hotScore;
    }

    public void setHotScore(Long hotScore) {
        this.hotScore = hotScore;
    }

    public List<SkuLsAttrValue> getSkuAttrValueList() {
        return skuAttrValueList;
    }

    public void setSkuAttrValueList(List<SkuLsAttrValue> skuAttrValueList) {
        this.skuAttrValueList = skuAttrValueList;
    }

}
