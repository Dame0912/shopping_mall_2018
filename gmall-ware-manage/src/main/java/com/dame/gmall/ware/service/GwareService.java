package com.dame.gmall.ware.service;

import com.dame.gmall.bean.WareInfo;
import com.dame.gmall.bean.WareOrderTask;
import com.dame.gmall.bean.WareSku;

import java.util.List;

public interface GwareService {

    /**
     * 根据skuid查询库存数量
     *
     * @param skuid
     * @return
     */
    public Integer getStockBySkuId(String skuid);

    /**
     * 判断是否有指定数量的库存
     *
     * @param skuid
     * @param num
     * @return
     */
    public boolean hasStockBySkuId(String skuid, Integer num);

    /**
     * 根据skuid查询仓库的信息
     *
     * @param skuid
     * @return
     */
    public List<WareInfo> getWareInfoBySkuid(String skuid);

    /**
     * 保存sku的库存信息
     *
     * @param wareSku
     */
    public void addWareSku(WareSku wareSku);

    /**
     * 订单出库操作，减库存和减锁定库存，通知订单系统订单出库
     *
     * @param wareOrderTask
     */
    public void deliveryStock(WareOrderTask wareOrderTask);

    /**
     * 保存 wareOrderTask 和 wareOrderTaskDetail
     *
     * @param wareOrderTask
     * @return
     */
    public WareOrderTask saveWareOrderTask(WareOrderTask wareOrderTask);

    /**
     * 拆单 **
     *
     * @param wareOrderTask
     * @return
     */
    public List<WareOrderTask> checkOrderSplit(WareOrderTask wareOrderTask);

    /**
     * 锁库存。
     *
     * @param wareOrderTask
     */
    public void lockStock(WareOrderTask wareOrderTask);

    /**
     * 查询 WareOrderTask
     *
     * @param wareOrderTask
     * @return
     */
    public List<WareOrderTask> getWareOrderTaskList(WareOrderTask wareOrderTask);

    /**
     * 获取所有sku的库存信息
     *
     * @return
     */
    public List<WareSku> getWareSkuList();

    /**
     * 获取所有的仓库信息
     *
     * @return
     */
    public List<WareInfo> getWareInfoList();
}
