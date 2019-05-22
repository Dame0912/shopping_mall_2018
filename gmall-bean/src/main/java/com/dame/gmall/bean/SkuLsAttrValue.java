package com.dame.gmall.bean;

import java.io.Serializable;

/**
 * ES中的平台属性
 */
public class SkuLsAttrValue implements Serializable {

    /**
     * 平台属性值ID
     */
    private String valueId;

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }
}
