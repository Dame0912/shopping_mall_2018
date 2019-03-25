package com.dame.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dame.gmall.bean.BaseAttrInfo;
import com.dame.gmall.bean.SkuLsParams;
import com.dame.gmall.bean.SkuLsResult;
import com.dame.gmall.service.ListService;
import com.dame.gmall.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class ListController {

    @Reference
    private ListService listService;

    @Reference
    private ManageService manageService;

    @RequestMapping("list.html")
    public String getList(SkuLsParams skuLsParams, ModelMap modelMap) {
        SkuLsResult skuLsResult = listService.search(skuLsParams);
        // 保存skuLsInfoList
        modelMap.addAttribute("skuLsInfoList", skuLsResult.getSkuLsInfoList());

        // 保存平台属性和平台属性值
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();
        List<BaseAttrInfo> attrList = manageService.getAttrList(attrValueIdList);
        modelMap.addAttribute("attrList", attrList);

        // 制作 url 连接
        String urlParam = makeUrlParam(skuLsParams);
        modelMap.addAttribute("urlParam", urlParam);

        return "list";
    }

    /**
     * 制作平台属性的 URL
     *
     * @param skuLsParams
     * @return
     */
    private String makeUrlParam(SkuLsParams skuLsParams) {
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
                if (urlParam.length() > 0) {
                    urlParam.append("&");
                }
                urlParam.append("valueId=").append(attValId);
            }
        }

        return urlParam.toString();
    }

}
