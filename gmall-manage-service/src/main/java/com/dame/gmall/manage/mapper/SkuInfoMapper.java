package com.dame.gmall.manage.mapper;

import com.dame.gmall.bean.SkuInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuInfoMapper extends Mapper<SkuInfo> {

    public List<SkuInfo> selectSkuInfoListBySpu(long spuId);
}
