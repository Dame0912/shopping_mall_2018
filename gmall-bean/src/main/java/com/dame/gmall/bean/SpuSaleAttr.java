package com.dame.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * spu的销售属性信息
 */
public class SpuSaleAttr implements Serializable {

    @Id
    @Column
    private String id;

    /**
     * spuId
     */
    @Column
    private String spuId;

    /**
     * base销售属性Id
     */
    @Column
    private String saleAttrId;

    /**
     * base销售属性名称，例如：颜色，版本，型号等
     */
    @Column
    private String saleAttrName;

    /**
     * spu销售属性值列表
     */
    @Transient
    private List<SpuSaleAttrValue> spuSaleAttrValueList;

    /**
     * spu销售属性值JSON格式
     */
    @Transient
    private Map spuSaleAttrValueJson;

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

    public String getSaleAttrName() {
        return saleAttrName;
    }

    public void setSaleAttrName(String saleAttrName) {
        this.saleAttrName = saleAttrName;
    }

    public List<SpuSaleAttrValue> getSpuSaleAttrValueList() {
        return spuSaleAttrValueList;
    }

    public void setSpuSaleAttrValueList(List<SpuSaleAttrValue> spuSaleAttrValueList) {
        this.spuSaleAttrValueList = spuSaleAttrValueList;
    }

    public Map getSpuSaleAttrValueJson() {
        return spuSaleAttrValueJson;
    }

    public void setSpuSaleAttrValueJson(Map spuSaleAttrValueJson) {
        this.spuSaleAttrValueJson = spuSaleAttrValueJson;
    }
}

