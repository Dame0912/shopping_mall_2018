package com.dame.gmall.manage.mapper;

import com.dame.gmall.bean.SpuSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {

    public List<SpuSaleAttr> selectSpuSaleAttrList(long spuId);

    public List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("skuId") long skuId, @Param("spuId") long spuId);
}