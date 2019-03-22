package com.dame.gmall.service;

import com.dame.gmall.bean.UserAddress;
import com.dame.gmall.bean.UserInfo;

import java.util.List;

public interface UserInfoService {

    List<UserInfo> findAll();

    // 根据userId 查询用户地址列表
    List<UserAddress> getUserAddressList(String userId);
}
