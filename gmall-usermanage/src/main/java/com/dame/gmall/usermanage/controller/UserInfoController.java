package com.dame.gmall.usermanage.controller;

import com.dame.gmall.bean.UserInfo;
import com.dame.gmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserInfoController {

    @Autowired
    UserInfoService userInfoService;

    @RequestMapping("findAll")
    @ResponseBody
    public List<UserInfo> findAll(){
        return userInfoService.findAll();
    }
}
