package com.dame.gmall.item.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {
    /**
     * 核心线程数
     */
    @Value("${corePoolSize:10}")
    private Integer corePoolSize;

    /**
     * 最大线程数
     */
    @Value("${maxPoolSize:50}")
    private Integer maxPoolSize;

    /**
     * 队列最大长度
     */
    @Value("${queueCapacity:1000}")
    private Integer queueCapacity;

    /**
     * 线程池维护线程允许的空闲时间
     */
    @Value("${keepAliveSeconds:300}")
    private Integer keepAliveSeconds;

    /**
     * 热度分数保存，执行的线程池
     *
     * @return
     */
    @Bean(name = {"hotScoreExecutor"})
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        poolTaskExecutor.setCorePoolSize(corePoolSize);
        poolTaskExecutor.setMaxPoolSize(maxPoolSize);
        poolTaskExecutor.setQueueCapacity(queueCapacity);
        poolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        return poolTaskExecutor;
    }
}
