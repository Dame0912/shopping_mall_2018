package com.dame.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * 平台属性值信息
 */
public class BaseAttrValue implements Serializable {

    @Id
    @Column
    private String id;
    /**
     * 平台属性值名称
     */
    @Column
    private String valueName;
    /**
     * 平台属性Id
     */
    @Column
    private String attrId;
    /**
     * 平台属性展示时候，点击平台属性使用
     */
    @Transient
    private String urlParam;

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }
}

