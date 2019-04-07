package com.dame.gmall.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

public class ActiveMQUtil {

    PooledConnectionFactory pooledConnectionFactory = null;

    public void init(String brokerUrl) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        pooledConnectionFactory = new PooledConnectionFactory(connectionFactory);
        // 设置超时时间
        pooledConnectionFactory.setExpiryTimeout(2000);
        // 设置出现异常时，重新连接
        pooledConnectionFactory.setReconnectOnException(true);
        // 设置最大连接数
        pooledConnectionFactory.setMaxConnections(5);
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = pooledConnectionFactory.createConnection();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return connection;
    }

}
