package com.dame.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 基本销售属性
 */
public class BaseSaleAttr implements Serializable {

    @Id
    @Column
    String id;

    /**
     * 销售属性名称（如：颜色）
     */
    @Column
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
