package com.dame.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * sku的平台属性信息
 */
public class SkuAttrValue implements Serializable {
    @Id
    @Column
    String id;
    /**
     * base 平台属性Id
     */
    @Column
    String attrId;
    /**
     * base 平台属性值Id
     */
    @Column
    String valueId;

    @Column
    String skuId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }
}
