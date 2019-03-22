package com.dame.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
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

