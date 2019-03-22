package com.dame.gmall.usermanage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dame.gmall.bean.UserAddress;
import com.dame.gmall.bean.UserInfo;
import com.dame.gmall.service.UserInfoService;
import com.dame.gmall.usermanage.mapper.UserAddressMapper;
import com.dame.gmall.usermanage.mapper.UserInfoMapper;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserAddressMapper userAddressMapper;

    @Override
    public List<UserInfo> findAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }
}
