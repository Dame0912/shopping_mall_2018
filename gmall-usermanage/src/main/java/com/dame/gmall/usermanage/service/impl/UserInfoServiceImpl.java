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
import org.springframework.util.StringUtils;
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
        // 数据中保存的密码是md5加密的
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
    public UserInfo verify(String userId) {
        Jedis jedis = redisUtil.getJedis();
        try {
            // 从redis中检查是否存在
            String redisKey = USER_INFO_KEY_PREFIX + userId + USER_INFO_KEY_SUFFIX;
            String userStr = jedis.get(redisKey);
            if (!StringUtils.isEmpty(userStr)) {
                //延长时效
                jedis.expire(redisKey, USER_INFO_KEY_TIMEOUT);
                UserInfo userInfo = JSON.parseObject(userStr, UserInfo.class);
                return userInfo;
            }
        } finally {
            jedis.close();
        }
        return null;
    }

    @Override
    public void logout(String userId) {
        Jedis jedis = redisUtil.getJedis();
        try {
            // 删除用户在redis中的数据
            String redisKey = USER_INFO_KEY_PREFIX + userId + USER_INFO_KEY_SUFFIX;
            jedis.del(redisKey);
        }finally {
            jedis.close();
        }
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }

}
