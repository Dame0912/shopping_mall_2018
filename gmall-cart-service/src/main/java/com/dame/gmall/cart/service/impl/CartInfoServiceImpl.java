package com.dame.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dame.gmall.bean.CartInfo;
import com.dame.gmall.bean.SkuInfo;
import com.dame.gmall.cart.constant.CartConst;
import com.dame.gmall.cart.mapper.CartInfoMapper;
import com.dame.gmall.config.RedisUtil;
import com.dame.gmall.service.CartInfoService;
import com.dame.gmall.service.ManageService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.*;

@Service
public class CartInfoServiceImpl implements CartInfoService {

    @Autowired
    private RedisUtil redisUtil;

    @Reference
    private ManageService manageService;

    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        // 1、查询购物车中是否有该商品
        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuId(skuId);
        cartInfo.setUserId(userId);
        CartInfo cartInfoExist = cartInfoMapper.selectOne(cartInfo);

        // 购物车中不存在该商品
        if (null == cartInfoExist) {
            // 2、保存到购物车中
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            CartInfo cartInfo1 = new CartInfo();
            cartInfo1.setSkuId(skuId);
            cartInfo1.setCartPrice(skuInfo.getPrice());
            cartInfo1.setSkuPrice(skuInfo.getPrice());
            cartInfo1.setSkuName(skuInfo.getSkuName());
            cartInfo1.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo1.setUserId(userId);
            cartInfo1.setSkuNum(skuNum);
            cartInfoMapper.insertSelective(cartInfo1);

            cartInfoExist = cartInfo1;
        }
        // 购物车存在该商品
        else {
            // 2、更新购物车，商品数量
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum() + skuNum);
            cartInfoMapper.updateByPrimaryKey(cartInfoExist);
        }

        // 3、保存至redis
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        jedis.hset(cartKey, skuId, JSON.toJSONString(cartInfoExist));

        // 4、设置购物车的过期时间 = 用户的过期时间
        setExpireAsUserInfo(userId, cartKey, jedis);
        jedis.close();
    }


    @Override
    public List<CartInfo> getCartList(String userId) {
        // 从redis中取得
        Jedis jedis = redisUtil.getJedis();
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        List<String> cartJsons = jedis.hvals(cartKey);

        if (cartJsons != null && cartJsons.size() > 0) {
            List<CartInfo> cartInfoList = new ArrayList<>();
            for (String cartJson : cartJsons) {
                CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);
                cartInfoList.add(cartInfo);
            }
            // 排序
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return Long.compare(Long.parseLong(o2.getId()), Long.parseLong(o1.getId()));
                }
            });
            return cartInfoList;
        } else {
            // 从数据库中查询
            return loadCartDB(userId);
        }
    }


    /**
     * 从数据库中查询，其中cart_price 可能是旧值，所以需要关联sku_info 表信息。
     *
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> loadCartDB(String userId) {
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCurPrice(userId);
        if (CollectionUtils.isEmpty(cartInfoList)) {
            return null;
        }
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        Map<String, String> map = new HashMap<>(cartInfoList.size());
        for (CartInfo cartInfo : cartInfoList) {
            String cartJson = JSON.toJSONString(cartInfo);
            map.put(cartInfo.getSkuId(), cartJson);
        }
        jedis.hmset(cartKey, map);

        // 设置购物车的过期时间 = 用户的过期时间
        setExpireAsUserInfo(userId, cartKey, jedis);
        jedis.close();
        return cartInfoList;
    }

    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListFromCookie, String userId) {
        List<CartInfo> cartInfoListDB = cartInfoMapper.selectCartListWithCurPrice(userId);
        // 循环开始匹配
        loopa:
        for (CartInfo cartInfoCookie : cartListFromCookie) {
            for (CartInfo cartInfoDB : cartInfoListDB) {
                if (cartInfoCookie.getSkuId().equals(cartInfoDB.getSkuId())) {
                    cartInfoDB.setSkuNum(cartInfoDB.getSkuNum() + cartInfoCookie.getSkuNum());
                    //更新数据库
                    cartInfoMapper.updateByPrimaryKey(cartInfoDB);
                    continue loopa;
                }
            }
            // 插入数据库
            cartInfoCookie.setUserId(userId);
            cartInfoMapper.insertSelective(cartInfoCookie);
        }
        // 查询数据库，即为最新的合并后的数据
        List<CartInfo> cartInfoList = loadCartDB(userId);
        // 如果cookie中的cartInfo为选中状态，则将redis中的状态也进行更新。未选中的不更新
        for (CartInfo cartInfo : cartInfoList) {
            for (CartInfo info : cartListFromCookie) {
                if (info.getSkuId().equals(cartInfo.getSkuId())) {
                    if ("1".equals(info.getIsChecked())) {
                        cartInfo.setIsChecked("1");
                        checkCart(cartInfo.getSkuId(), "1", cartInfo.getUserId());
                    }
                }
            }
        }
        return cartInfoList;
    }

    /**
     * 把对应skuId的购物车的信息从redis中取出来，反序列化，修改isChecked标志。再保存回redis中。
     * 同时保存另一个 redis的 key专门用来存储用户选中的商品，方便结算页面使用。
     *
     * @param skuId
     * @param isChecked 1:选中  0:未选中
     * @param userId
     */
    @Override
    public void checkCart(String skuId, String isChecked, String userId) {
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        String cartJson = jedis.hget(cartKey, skuId);
        CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);
        cartInfo.setIsChecked(isChecked);
        String cartCheckdJson = JSON.toJSONString(cartInfo);
        jedis.hset(cartKey, skuId, cartCheckdJson);

        // 新增到，已选择的redis中
        String userCheckedKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CHECKED_KEY_SUFFIX;
        if ("1".equals(isChecked)) {
            jedis.hset(userCheckedKey, skuId, cartCheckdJson);
        } else {
            jedis.hdel(userCheckedKey, skuId);
        }
        setExpireAsUserInfo(userId, userCheckedKey, jedis);
        jedis.close();
    }

    /**
     * 得到选中的购物车列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        // 获得redis中的key
        String userCheckedKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CHECKED_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        List<String> cartCheckedList = jedis.hvals(userCheckedKey);
        List<CartInfo> newCartList = new ArrayList<>();
        for (String cartJson : cartCheckedList) {
            CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);
            newCartList.add(cartInfo);
        }
        return newCartList;
    }

    /**
     * 设置过期时间 = 用户的过期时间
     *
     * @param userId
     * @param redisKey
     * @param jedis
     */
    private void setExpireAsUserInfo(String userId, String redisKey, Jedis jedis) {
        String userKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USERINFOKEY_SUFFIX;
        Long userTTL = jedis.ttl(userKey);
        jedis.expire(redisKey, userTTL.intValue());
    }

}
