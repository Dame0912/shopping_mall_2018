package com.dame.gmall.usermanage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dame.gmall.bean.UserAddress;
import com.dame.gmall.bean.UserInfo;
import com.dame.gmall.config.RedisUtil;
import com.dame.gmall.service.UserInfoService;
import com.dame.gmall.usermanage.mapper.UserAddressMapper;
import com.dame.gmall.usermanage.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * redis中保存用户信息的key
     */
    public String USER_INFO_KEY_PREFIX = "user:";
    public String USER_INFO_KEY_SUFFIX = ":info";
    public int USER_INFO_KEY_TIMEOUT = 60 * 60;


    @Override
    public UserInfo login(UserInfo userInfo) {
        String digestPwd = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        userInfo.setPasswd(digestPwd);
        UserInfo selectUser = userInfoMapper.selectOne(userInfo);
        if (null != selectUser) {
            // 将用户信息保存到Redis中
            Jedis jedis = redisUtil.getJedis();
            jedis.setex(USER_INFO_KEY_PREFIX + selectUser.getId() + USER_INFO_KEY_SUFFIX, USER_INFO_KEY_TIMEOUT, JSON.toJSONString(selectUser));
            jedis.close();
            return selectUser;
        }
        return null;
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }
}
