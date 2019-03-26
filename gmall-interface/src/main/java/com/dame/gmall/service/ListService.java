package com.dame.gmall.service;

import com.dame.gmall.bean.SkuLsInfo;
import com.dame.gmall.bean.SkuLsParams;
import com.dame.gmall.bean.SkuLsResult;

public interface ListService {

    // 保存sku到es中
    public void saveSkuInfo(SkuLsInfo skuLsInfo);

    // 从es中查询数据
    public SkuLsResult search(SkuLsParams skuLsParams);

    // 保存商品的热度分数
    public void incrHotScore(String skuId);
}
