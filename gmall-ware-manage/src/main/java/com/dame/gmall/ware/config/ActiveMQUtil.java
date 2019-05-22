package com.dame.gmall.ware.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * MQ 工具类
 *
 * @param
 * @return
 */
public class ActiveMQUtil {

    private PooledConnectionFactory poolFactory;

    /**
     * 初始化MQ连接池工厂
     *
     * @param brokerUrl
     */
    public void init(String brokerUrl) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        poolFactory = new PooledConnectionFactory(factory);
        poolFactory.setMaxConnections(5);
        poolFactory.setReconnectOnException(true);
        // 后台对象清理时，休眠时间超过了10000毫秒的对象为过期
        poolFactory.setTimeBetweenExpirationCheckMillis(10000);
    }

    /**
     * 获取连接
     *
     * @return
     */
    public Connection getConn() {
        Connection connection = null;
        try {
            connection = poolFactory.createConnection();

        } catch (JMSException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
