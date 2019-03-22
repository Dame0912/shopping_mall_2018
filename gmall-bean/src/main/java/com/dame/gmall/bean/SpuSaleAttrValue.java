package com.dame.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * spu销售属性值信息
 */
public class SpuSaleAttrValue implements Serializable {
    @Id
    @Column
    private String id;

    @Column
    private String spuId;
    /**
     * Base销售属性Id
     */
    @Column
    private String saleAttrId;
    /**
     * spu销售属性值名称
     */
    @Column
    private String saleAttrValueName;
    /**
     * 对应的sku是否有该销售属性值，有：1；无：0
     */
    @Transient
    private String isChecked;

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

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

    public String getSaleAttrId() {
        return saleAttrId;
    }

    public void setSaleAttrId(String saleAttrId) {
        this.saleAttrId = saleAttrId;
    }

    public String getSaleAttrValueName() {
        return saleAttrValueName;
    }

    public void setSaleAttrValueName(String saleAttrValueName) {
        this.saleAttrValueName = saleAttrValueName;
    }
}

