package com.dame.gmall.service;

import com.dame.gmall.bean.UserAddress;
import com.dame.gmall.bean.UserInfo;

import java.util.List;

public interface UserInfoService {

    // 用户登录
    public UserInfo login(UserInfo userInfo);

    // 验证用户信息，从redis中查询是否有用户信息，有就延长时效
    public UserInfo verify(String userId);

    // 用户退出登录
    public void logout(String userId);

    // 根据userId 查询用户地址列表
    public List<UserAddress> getUserAddressList(String userId);

}
