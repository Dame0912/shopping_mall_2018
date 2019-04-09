package com.dame.gmall.bean;

import javax.persistence.*;

/**
 * sku库存明细
 * sku与仓库关联表
 *
 * @param
 * @return
 */
public class WareSku {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Id
    private String id;

    /**
     * skuId
     */
    @Column
    private String skuId;

    /**
     * 仓库id
     */
    @Column
    private String warehouseId;

    /**
     * 库存数
     */
    @Column
    private Integer stock = 0;

    /**
     * 存货名称
     */
    @Column
    private String stockName;

    /**
     * 锁定库存数
     */
    @Column
    private Integer stockLocked;

    /**
     * 仓库名称
     */
    @Transient
    private String warehouseName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public Integer getStockLocked() {
        return stockLocked;
    }

    public void setStockLocked(Integer stockLocked) {
        this.stockLocked = stockLocked;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }
}
