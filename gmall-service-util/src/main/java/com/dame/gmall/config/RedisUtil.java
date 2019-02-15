package com.dame.gmall.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
    private JedisPool jedisPool;

    public void initJedisPool(String host, int port, int database) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 总数
        jedisPoolConfig.setMaxTotal(200);
        // 获取连接时等待的最大毫秒数
        jedisPoolConfig.setMaxWaitMillis(10 * 1000);
        // 最小剩余数
        jedisPoolConfig.setMaxIdle(10);
        // 如果到最大数，是否阻塞
        jedisPoolConfig.setBlockWhenExhausted(true);
        // 再获取连接时，检查是否有效
        jedisPoolConfig.setTestOnBorrow(true);

        jedisPool = new JedisPool(jedisPoolConfig, host, port, 20 * 1000);
    }

    public Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }
}
