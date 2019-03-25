package com.dame.gmall.manage.mapper;

import com.dame.gmall.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {
    // 根据三级分类id查询属性表
    List<BaseAttrInfo> getBaseAttrInfoListByCatalog3Id(Long catalog3Id);

    // 根据平台属性值集合，查询平台属性集合
    List<BaseAttrInfo> getBaseAttrInfoListByAttrValueIds(@Param("attrValIds") String attrValIds);
}
