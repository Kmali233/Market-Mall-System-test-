package com.hmall.common.config;

import com.hmall.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@ConditionalOnClass(RabbitTemplate.class)
@EnableConfigurationProperties(MqConfigProperties.class)
@RequiredArgsConstructor
public class MqConfig implements BeanPostProcessor {

    private final MqConfigProperties properties;

    @Bean
    @ConditionalOnMissingBean(ConnectionFactory.class)
    public CachingConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(properties.getHost());
        factory.setPort(properties.getPort());
        factory.setVirtualHost(properties.getVhost());
        factory.setUsername(properties.getUsername());
        factory.setPassword(properties.getPassword());
        return factory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RabbitTemplate) {
            RabbitTemplate rabbitTemplate = (RabbitTemplate) bean;
            rabbitTemplate.addBeforePublishPostProcessors(message -> {
                Long userId = UserContext.getUser();
                if (userId != null) {
                    message.getMessageProperties().setHeader("user-info", userId.toString());
                }
                return message;
            });
        }
        if (bean instanceof SimpleRabbitListenerContainerFactory) {
            SimpleRabbitListenerContainerFactory factory = (SimpleRabbitListenerContainerFactory) bean;
            factory.setAfterReceivePostProcessors(message -> {
                Object userId = message.getMessageProperties().getHeader("user-info");
                if (userId != null) {
                    UserContext.setUser(Long.valueOf(userId.toString()));
                }
                return message;
            });
            factory.setAdviceChain(new UserContextCleanupAdvice());
            configureRetry(factory);
        }
        return bean;
    }

    private void configureRetry(SimpleRabbitListenerContainerFactory factory) {
        MqConfigProperties.Retry retryProps = properties.getListener().getRetry();
        if (Boolean.FALSE.equals(retryProps.getEnable())) {
            return;
        }
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(retryProps.getMaxAttempts());
        retryTemplate.setRetryPolicy(retryPolicy);

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(retryProps.getInterval().toMillis());
        backOffPolicy.setMultiplier(retryProps.getMultiplier());
        retryTemplate.setBackOffPolicy(backOffPolicy);

        factory.setRetryTemplate(retryTemplate);
    }

    static class UserContextCleanupAdvice implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            try {
                return invocation.proceed();
            } finally {
                UserContext.removeUser();
            }
        }
    }
}
