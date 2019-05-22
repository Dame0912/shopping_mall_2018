package com.dame.gmall.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * spu信息
 */
public class SpuInfo implements Serializable {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * spu名称
     */
    @Column
    private String spuName;

    /**
     * 描述
     */
    @Column
    private String description;

    /**
     * 三级分类ID
     */
    @Column
    private String catalog3Id;

    /**
     * 销售属性信息
     */
    @Transient
    private List<SpuSaleAttr> spuSaleAttrList;

    /**
     * spu的图片信息
     */
    @Transient
    private List<SpuImage> spuImageList;


    public List<SpuSaleAttr> getSpuSaleAttrList() {
        return spuSaleAttrList;
    }

    public void setSpuSaleAttrList(List<SpuSaleAttr> spuSaleAttrList) {
        this.spuSaleAttrList = spuSaleAttrList;
    }

    public List<SpuImage> getSpuImageList() {
        return spuImageList;
    }

    public void setSpuImageList(List<SpuImage> spuImageList) {
        this.spuImageList = spuImageList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpuName() {
        return spuName;
    }

    public void setSpuName(String spuName) {
        this.spuName = spuName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }
}
