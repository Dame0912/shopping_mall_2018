package com.dame.gmall.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 查询ES，结果封装的dto
 */
public class SkuLsResult implements Serializable {

    /**
     * 查询es中的skulsInfo集合
     */
    private List<SkuLsInfo> skuLsInfoList;

    /**
     * 查询出的总数量
     */
    private long total;

    /**
     * 查询出的总页数
     */
    private long totalPages;

    /**
     * 平台属性值集合
     */
    private List<String> attrValueIdList;

    public List<SkuLsInfo> getSkuLsInfoList() {
        return skuLsInfoList;
    }

    public void setSkuLsInfoList(List<SkuLsInfo> skuLsInfoList) {
        this.skuLsInfoList = skuLsInfoList;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public List<String> getAttrValueIdList() {
        return attrValueIdList;
    }

    public void setAttrValueIdList(List<String> attrValueIdList) {
        this.attrValueIdList = attrValueIdList;
    }
}
