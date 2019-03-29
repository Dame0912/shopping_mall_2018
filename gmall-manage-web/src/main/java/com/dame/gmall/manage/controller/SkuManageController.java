package com.dame.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dame.gmall.bean.SkuInfo;
import com.dame.gmall.bean.SpuImage;
import com.dame.gmall.bean.SpuSaleAttr;
import com.dame.gmall.bean.SpuSaleAttrValue;
import com.dame.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SkuManageController {

    @Reference
    private ManageService manageService;

    /**
     * 查询图片列表
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "spuImageList", method = RequestMethod.GET)
    @ResponseBody
    public List<SpuImage> getSpuImageList(@RequestParam Map<String, String> map) {
        String spuId = map.get("spuId");
        List<SpuImage> spuImageList = manageService.getSpuImageList(spuId);
        return spuImageList;
    }

    /**
     * 获取销售属性列表
     *
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<SpuSaleAttr> getSpuSaleAttrList(HttpServletRequest httpServletRequest) {
        String spuId = httpServletRequest.getParameter("spuId");
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrList(spuId);
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            Map map = new HashMap();
            map.put("total", spuSaleAttrValueList.size());
            map.put("rows", spuSaleAttrValueList);
            spuSaleAttr.setSpuSaleAttrValueJson(map);
        }
        return spuSaleAttrList;
    }

    /**
     * 保存 SKu 信息
     * @param skuInfo
     * @return
     */
    @RequestMapping("saveSku")
    @ResponseBody
    public Map saveSku(SkuInfo skuInfo) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        try{
            manageService.saveSku(skuInfo);
        }catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }
}
