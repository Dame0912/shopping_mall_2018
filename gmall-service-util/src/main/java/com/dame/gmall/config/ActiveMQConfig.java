package com.dame.gmall.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import javax.jms.Session;

@Configuration
public class ActiveMQConfig {

    @Value("${spring.activemq.broker-url:disabled}")
    private String brokerURL;
    
    @Value("${activemq.listener.enable:disabled}")
    private String listenerEnable;

    @Bean
    public ActiveMQUtil activeMQUtil() {
        if ("disabled".equals(brokerURL)) {
            return null;
        }
        ActiveMQUtil activeMQUtil = new ActiveMQUtil();
        activeMQUtil.init(brokerURL);
        return activeMQUtil;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory(ActiveMQConnectionFactory activeMQConnectionFactory) {
        if ("disabled".equals(listenerEnable)) {
            return null;
        }
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // 设置工厂
        factory.setConnectionFactory(activeMQConnectionFactory);
        // 设置事务
        factory.setSessionTransacted(false);
        // 手动签收
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        // 设置并发数
        factory.setConcurrency("5");
        // 重连间隔时间
        factory.setRecoveryInterval(5000L);
        return factory;
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(brokerURL);
        return activeMQConnectionFactory;
    }
}
