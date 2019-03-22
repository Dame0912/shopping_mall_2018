package com.dame.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dame.gmall.bean.SkuLsParams;
import com.dame.gmall.bean.SkuLsResult;
import com.dame.gmall.service.ListService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ListController {

    @Reference
    private ListService listService;

    @RequestMapping("list")
    @ResponseBody
    public String getList(SkuLsParams skuLsParams){
        SkuLsResult search = listService.search(skuLsParams);
        return JSON.toJSONString(search);
    }

}
