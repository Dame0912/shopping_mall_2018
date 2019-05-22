package com.dame.gmall.bean;

import java.io.Serializable;

/**
 * 检索商品入参dto
 */
public class SkuLsParams implements Serializable {

    /**
     * 三级分类Id
     */
    private String catalog3Id;

    /**
     * 搜索框输入的关键字
     */
    private String keyword;

    /**
     * 平台属性值ID
     */
    private String[] valueId;

    /**
     * 分页展示的页码
     */
    private int pageNo = 1;

    /**
     * 分页展示，每页展示的数量
     */
    private int pageSize = 10;

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String[] getValueId() {
        return valueId;
    }

    public void setValueId(String[] valueId) {
        this.valueId = valueId;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
