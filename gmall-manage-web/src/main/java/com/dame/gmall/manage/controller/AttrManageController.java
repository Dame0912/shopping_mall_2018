package com.dame.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dame.gamll.bean.*;
import com.dame.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
    ManageService manageService;

    @RequestMapping("attrListPage")
    public String getAttrListPage(){
        return "attrListPage";
    }

    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<BaseCatalog1> getCatalog1(HttpServletRequest request){
        return manageService.getCatalog1();
    }

    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<BaseCatalog2> getCatalog2(HttpServletRequest request){
        String catalog1Id = request.getParameter("catalog1Id");
        return manageService.getCatalog2(catalog1Id);
    }

    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<BaseCatalog3> getCatalog3(HttpServletRequest request){
        String catalog2Id = request.getParameter("catalog2Id");
        return manageService.getCatalog3(catalog2Id);
    }

    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<BaseAttrInfo> getAttrInfoList(HttpServletRequest request){
        String catalog3Id = request.getParameter("catalog3Id");
        return manageService.getAttrList(catalog3Id);
    }

    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public Map saveAttrInfo(BaseAttrInfo baseAttrInfo){
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        try {
            manageService.saveAttrInfo(baseAttrInfo);
        } catch (Exception e) {
            map.put("success",false);
        }
        return map;
    }

    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<BaseAttrValue> getAttrValueList(HttpServletRequest request){
        String attrId = request.getParameter("attrId");
        return manageService.getAttrInfo(attrId).getAttrValueList();
    }

}
