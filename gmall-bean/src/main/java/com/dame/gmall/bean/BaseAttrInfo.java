package com.dame.gmall.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 平台属性信息
 */
public class BaseAttrInfo implements Serializable {

    // 获取主键自增atuo
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    /**
     * 平台属性名称（如：内存）
     */
    @Column
    private String attrName;
    /**
     * 三级分类Id
     */
    @Column
    private String catalog3Id;
    /**
     * 平台属性值列表
     */
    @Transient
    private List<BaseAttrValue> attrValueList;

    public List<BaseAttrValue> getAttrValueList() {
        return attrValueList;
    }

    public void setAttrValueList(List<BaseAttrValue> attrValueList) {
        this.attrValueList = attrValueList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }
}
