package com.dame.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dame.gmall.bean.*;
import com.dame.gmall.service.ListService;
import com.dame.gmall.service.ManageService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台属性管理
 */
@Controller
public class AttrManageController {

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;

    /**
     * 属性列表页面
     *
     * @return
     */
    @RequestMapping("attrListPage")
    public String getAttrListPage() {
        return "attrListPage";
    }

    /**
     * 获取所有的一级分类信息
     * @return
     */
    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<BaseCatalog1> getCatalog1() {
        return manageService.getCatalog1();
    }

    /**
     * 获取二级分类信息
     * @param request
     * @return
     */
    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<BaseCatalog2> getCatalog2(HttpServletRequest request) {
        String catalog1Id = request.getParameter("catalog1Id");
        return manageService.getCatalog2(catalog1Id);
    }

    /**
     * 获取三级分类信息
     * @param request
     * @return
     */
    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<BaseCatalog3> getCatalog3(HttpServletRequest request) {
        String catalog2Id = request.getParameter("catalog2Id");
        return manageService.getCatalog3(catalog2Id);
    }

    /**
     * 根据三级分类id，查询出平台属性
     * @param request
     * @return
     */
    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<BaseAttrInfo> getAttrInfoList(HttpServletRequest request) {
        String catalog3Id = request.getParameter("catalog3Id");
        return manageService.getAttrList(catalog3Id);
    }

    /**
     * 保存添加的平台属性含平台属性值
     * @param baseAttrInfo
     * @return
     */
    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public Map saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        try {
            manageService.saveAttrInfo(baseAttrInfo);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<BaseAttrValue> getAttrValueList(HttpServletRequest request) {
        String attrId = request.getParameter("attrId");
        return manageService.getAttrInfo(attrId).getAttrValueList();
    }

    /**
     * 商品上架，也就是将数据插入es中
     *
     * @param skuId
     */
    @RequestMapping(value = "onSale", method = RequestMethod.GET)
    @ResponseBody
    public void onSale(String skuId) {
        try {
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            SkuLsInfo skuLsInfo = new SkuLsInfo();
            BeanUtils.copyProperties(skuLsInfo, skuInfo);
            listService.saveSkuInfo(skuLsInfo);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("...AttrManagerController...onSale...失败...");
        }
    }

}
