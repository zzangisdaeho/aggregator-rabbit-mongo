package com.example.aggregatormongo.timer.redisson;

import org.redisson.api.ExecutorOptions;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RedissonClient;
import org.redisson.api.WorkerOptions;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class RedissonScheduledExecutorConfig {

    private static final String EXECUTOR_SERVICE_NAME = "rScheduledExecutor";

    @Bean(destroyMethod = "")
    public RScheduledExecutorService rScheduledExecutorService(
            final RedissonClient redissonClient,
            final BeanFactory beanFactory
    ) {
        final WorkerOptions workerOptions = WorkerOptions.defaults().workers(1).beanFactory(beanFactory);
        final ExecutorOptions executorOptions = ExecutorOptions.defaults()
                .taskRetryInterval(3, TimeUnit.SECONDS);
        final RScheduledExecutorService executorService = redissonClient
                .getExecutorService(EXECUTOR_SERVICE_NAME, executorOptions);
        executorService.registerWorkers(workerOptions);
        return executorService;
    }

}