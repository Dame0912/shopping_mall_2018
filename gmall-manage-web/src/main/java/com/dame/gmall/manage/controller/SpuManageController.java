package com.dame.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dame.gmall.bean.BaseSaleAttr;
import com.dame.gmall.bean.SpuInfo;
import com.dame.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品信息管理
 */
@Controller
public class SpuManageController {

    @Reference
    private ManageService manageService;

    @RequestMapping("spuListPage")
    public String getSpuListPage() {
        return "spuListPage";
    }

    /**
     * 根据 三级分类ID 查询 spuList
     *
     * @param request
     * @return
     */
    @RequestMapping("spuList")
    @ResponseBody
    public List<SpuInfo> spuList(HttpServletRequest request) {
        String catalog3Id = request.getParameter("catalog3Id");
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);
        return manageService.getSpuInfoList(spuInfo);
    }

    /**
     * 获取销售属性列表，字典表
     *
     * @return
     */
    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return manageService.getBaseSaleAttrList();
    }

    /**
     * 保存SpuInfo信息
     *
     * @param spuInfo
     * @return
     */
    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public Map saveSpuInfo(SpuInfo spuInfo) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        try {
            manageService.saveSpuInfo(spuInfo);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

}
