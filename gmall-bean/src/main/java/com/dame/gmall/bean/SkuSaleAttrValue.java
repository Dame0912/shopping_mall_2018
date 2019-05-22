package com.dame.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * sku销售属性信息
 */
public class SkuSaleAttrValue implements Serializable {

    @Id
    @Column
    private String id;

    /**
     * skuId
     */
    @Column
    private String skuId;

    /**
     * base销售属性Id
     */
    @Column
    private String saleAttrId;

    /**
     * spu的销售属性值Id
     */
    @Column
    String saleAttrValueId;

    /**
     * spu的销售属性名称（如：颜色）
     */
    @Column
    String saleAttrName;

    /**
     * spu的销售值属性名称（如：黑色）
     */
    @Column
    String saleAttrValueName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getSaleAttrId() {
        return saleAttrId;
    }

    public void setSaleAttrId(String saleAttrId) {
        this.saleAttrId = saleAttrId;
    }

    public String getSaleAttrValueId() {
        return saleAttrValueId;
    }

    public void setSaleAttrValueId(String saleAttrValueId) {
        this.saleAttrValueId = saleAttrValueId;
    }

    public String getSaleAttrName() {
        return saleAttrName;
    }

    public void setSaleAttrName(String saleAttrName) {
        this.saleAttrName = saleAttrName;
    }

    public String getSaleAttrValueName() {
        return saleAttrValueName;
    }

    public void setSaleAttrValueName(String saleAttrValueName) {
        this.saleAttrValueName = saleAttrValueName;
    }
}
