package com.dame.gmall.service;

import com.dame.gmall.bean.*;

import java.util.List;

public interface ManageService {
    /**
     * 获取所有的一级分类
     */
    public List<BaseCatalog1> getCatalog1();
    /**
     * 根据一级分类Id，查询二级分类
     */
    public List<BaseCatalog2> getCatalog2(String catalog1Id);
    /**
     *  根据二级分类Id，查询三级分类
     */
    public List<BaseCatalog3> getCatalog3(String catalog2Id);
    /**
     *  根据三级分类Id，查询平台属性列表
     */
    public List<BaseAttrInfo> getAttrList(String catalog3Id);
    /**
     *  保存平台属性，以及平台属性值
     */
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo);
    /**
     *  根据平台属性Id，获取平台属性对象
     */
    public BaseAttrInfo getAttrInfo(String attrId);
    /**
     *  根据三级分类Id，查询SpuInfo列表
     */
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);
    /**
     *  获取所有的销售属性列表
     */
    public List<BaseSaleAttr> getBaseSaleAttrList();
    /**
     *  大保存---spuInfo
     */
    public void saveSpuInfo(SpuInfo spuInfo);
    /**
     *  根据spuId查询出图片列表
     */
    public List<SpuImage> getSpuImageList(String spuId);
    /**
     *  根据spuId获取销售属性以及销售属性值
     */
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId);
    /**
     *  保存SKU信息
     */
    public void saveSku(SkuInfo skuInfo);
    /**
     *  根据skuId获取SkuInfo信息
     */
    public SkuInfo getSkuInfo(String skuId);
    /**
     *  根据该sku获取到spu销售属性的信息，如果spu中包含一个isChecked字段，sku中有该属性，则ischecked = 1
     */
    public List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(SkuInfo skuInfo);
    /**
     *  根据spuId查询出旗下的所有sku销售属性值
     */
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);
}
