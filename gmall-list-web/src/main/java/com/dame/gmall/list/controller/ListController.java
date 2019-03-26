package com.dame.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dame.gmall.bean.BaseAttrInfo;
import com.dame.gmall.bean.BaseAttrValue;
import com.dame.gmall.bean.SkuLsParams;
import com.dame.gmall.bean.SkuLsResult;
import com.dame.gmall.service.ListService;
import com.dame.gmall.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class ListController {

    @Reference
    private ListService listService;

    @Reference
    private ManageService manageService;

    @RequestMapping("list.html")
    public String getList(SkuLsParams skuLsParams, ModelMap modelMap) {
        // 从ES中查询结果
        SkuLsResult skuLsResult = listService.search(skuLsParams);
        // 保存skuLsInfoList
        modelMap.addAttribute("skuLsInfoList", skuLsResult.getSkuLsInfoList());

        // 保存平台属性和平台属性值
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();
        List<BaseAttrInfo> attrList = manageService.getAttrList(attrValueIdList);

        // 制作 url 连接
        String urlParam = makeUrlParam(skuLsParams);

        // 面包屑功能，声明个变量存储
        List<BaseAttrValue> baseAttrValuesList = new ArrayList<>();

        // 过滤重复的属性值id
        // 集合-- 能否在遍历的过程中进行删除集合中的数据？  不能
        for (Iterator<BaseAttrInfo> iterator = attrList.iterator(); iterator.hasNext(); ) {
            BaseAttrInfo baseAttrInfo = iterator.next();
            List<BaseAttrValue> baseAttrValueList = baseAttrInfo.getAttrValueList();
            for (BaseAttrValue baseAttrValue : baseAttrValueList) {
                if (null != skuLsParams.getValueId() && skuLsParams.getValueId().length > 0) {
                    for (String paramBaseAttrValId : skuLsParams.getValueId()) {
                        if (paramBaseAttrValId.equals(baseAttrValue.getId())) {
                            // 将该平台属性从平台属性列表中移除
                            iterator.remove();
                            // 存放每一个面包屑
                            BaseAttrValue baseAttrValueSelected = new BaseAttrValue();
                            // 平台属性名称:平台属性值名称
                            baseAttrValueSelected.setValueName(baseAttrInfo.getAttrName() + ":" + baseAttrValue.getValueName());
                            // 制作URL，为了点击该面包屑的时候，祛除该属性选择
                            String selectedUrl = makeUrlParam(skuLsParams, paramBaseAttrValId);
                            baseAttrValueSelected.setUrlParam(selectedUrl);
                            baseAttrValuesList.add(baseAttrValueSelected);
                        }
                    }
                }
            }
        }

        modelMap.addAttribute("totalPages", skuLsResult.getTotalPages());//分页
        modelMap.addAttribute("pageNo", skuLsParams.getPageNo());
        modelMap.addAttribute("attrList", attrList);//sku列表
        modelMap.addAttribute("urlParam", urlParam);//供平台属性拼接URL
        modelMap.addAttribute("baseAttrValuesList", baseAttrValuesList);//面包屑
        modelMap.addAttribute("keyword", skuLsParams.getKeyword());//关键字
        return "list";
    }

    /**
     * 制作平台属性的 URL
     *
     * @param skuLsParams
     * @return
     */
    private String makeUrlParam(SkuLsParams skuLsParams, String... selectedValId) {
        //  http://list.gmall.com/list.html?keyword=小米&catalog3Id=61&valueId=13&pageNo=1&pageSize=10
        StringBuilder urlParam = new StringBuilder();
        if (StringUtils.isNotEmpty(skuLsParams.getKeyword())) {
            urlParam.append("keyword=").append(skuLsParams.getKeyword());
        }

        if (StringUtils.isNotEmpty(skuLsParams.getCatalog3Id())) {
            if (urlParam.length() > 0) {
                urlParam.append("&");
            }
            urlParam.append("catalog3Id=").append(skuLsParams.getCatalog3Id());
        }

        if (null != skuLsParams.getValueId() && skuLsParams.getValueId().length > 0) {
            for (String attValId : skuLsParams.getValueId()) {
                if (null != selectedValId && selectedValId.length > 0) {
                    String selValId = selectedValId[0];
                    if (attValId.equals(selValId)) {
                        continue;
                    }
                }

                if (urlParam.length() > 0) {
                    urlParam.append("&");
                }
                urlParam.append("valueId=").append(attValId);
            }
        }

        return urlParam.toString();
    }

}
