package com.dame.gmall.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *  ES 中保存的Sku信息
 */
public class SkuLsInfo implements Serializable {

    /**
     * es的document id，使用数据库中的id
     */
    private String id;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * sku名称
     */
    private String skuName;

    /**
     * sku描述
     */
    private String skuDesc;

    /**
     * 三级分类Id
     */
    private String catalog3Id;

    /**
     * 默认图片路径
     */
    private String skuDefaultImg;

    /**
     * 热度分数
     */
    private Long hotScore = 0L;

    /**
     * 平台属性集合
     */
    private List<SkuLsAttrValue> skuAttrValueList;

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
