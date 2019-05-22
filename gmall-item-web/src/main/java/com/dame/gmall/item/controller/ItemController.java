package com.dame.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dame.gmall.bean.SkuInfo;
import com.dame.gmall.bean.SkuSaleAttrValue;
import com.dame.gmall.bean.SpuSaleAttr;
import com.dame.gmall.service.ListService;
import com.dame.gmall.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    public static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;

    @Autowired
    @Qualifier("hotScoreExecutor")
    private ThreadPoolTaskExecutor hotScoreTaskExecutor;

    /**
     * 查询订单详情
     * @param skuId
     * @param modelMap
     * @return
     */
    @RequestMapping("{skuId}.html")
    public String skuInfoPage(@PathVariable(value = "skuId") String skuId, ModelMap modelMap) {
        LOGGER.info("skuInfoPage开始，入参SkuId：{}", skuId);
        // skuInfo信息
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        modelMap.addAttribute("skuInfo", skuInfo);

        // 查询出该sku对应spu的销售属性信息
        List<SpuSaleAttr> spuSaleAttrList = manageService.selectSpuSaleAttrListCheckBySku(skuInfo);
        modelMap.addAttribute("spuSaleAttrList", spuSaleAttrList);

        // 通过spuId查询出所有的sku销售属性值
        List<SkuSaleAttrValue> skuSaleAttrValueListBySpu = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        // 把列表变换成 valueid1|valueid2|valueid3 ：skuId  用于在页面中定位查询
        Map<String, String> map = new HashMap<>();
        StringBuffer jsonKey = new StringBuffer();
        for (int i = 0; i < skuSaleAttrValueListBySpu.size(); i++) {
            if (StringUtils.isNotEmpty(jsonKey)) {
                jsonKey.append("|");
            }
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueListBySpu.get(i);
            jsonKey.append(skuSaleAttrValue.getSaleAttrValueId());
            if ((i + 1) == skuSaleAttrValueListBySpu.size() ||
                    !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueListBySpu.get(i + 1).getSkuId())) {
                map.put(jsonKey.toString(), skuSaleAttrValue.getSkuId());
                // 清空StringBuffer
                jsonKey.setLength(0);
            }
        }
        String valuesSkuJson = JSON.toJSONString(map);
        modelMap.addAttribute("valuesSkuJson", valuesSkuJson);

        // 保存热度分数
        hotScoreTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                listService.incrHotScore(skuId);
            }
        });

        LOGGER.info("skuInfoPage结束，出参valuesSkuJson：{}", valuesSkuJson);
        return "item";
    }
}
