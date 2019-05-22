package com.dame.gmall.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ManageController {

    /**
     * 后台管理首页
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "index";
    }

}
