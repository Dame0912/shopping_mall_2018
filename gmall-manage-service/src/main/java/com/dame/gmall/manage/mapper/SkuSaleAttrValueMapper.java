package com.dame.gmall.manage.mapper;

import com.dame.gmall.bean.SkuSaleAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {

    public List<SkuSaleAttrValue> selectSkuSaleAttrValueListBySpu(@Param("spuId") String spuId);
}

