package com.dame.gmall.service;

import com.dame.gmall.bean.UserAddress;
import com.dame.gmall.bean.UserInfo;

import java.util.List;

public interface UserInfoService {

    // 用户登录
    public UserInfo login(UserInfo userInfo);

    // 根据userId 查询用户地址列表
    public List<UserAddress> getUserAddressList(String userId);
}
