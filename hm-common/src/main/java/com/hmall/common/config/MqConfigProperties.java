package com.hmall.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * RabbitMQ配置属性类
 * 用于从配置文件中读取hm.mq前缀的配置项，提供RabbitMQ连接信息和消费者重试策略配置
 */
@Data
@ConfigurationProperties(prefix = "hm.mq")
public class MqConfigProperties {
    /** RabbitMQ服务器主机地址 */
    private String host = "xxx";
    
    /** RabbitMQ服务器端口号 */
    private Integer port = 5672;
    
    /** RabbitMQ虚拟主机路径 */
    private String vhost = "/hmxt";
    
    /** RabbitMQ连接用户名 */
    private String username = "xxx";
    
    /** RabbitMQ连接密码 */
    private String password = "xxx";
    
    /** 消息监听器配置 */
    private Listener listener = new Listener();

    /**
     * 消息监听器配置内部类
     */
    @Data
    public static class Listener {
        /** 消息消费重试策略配置 */
        private Retry retry = new Retry();
    }

    /**
     * 消息消费重试策略配置内部类
     */
    @Data
    public static class Retry {
        private Boolean enable = true;  // 是否启用重试机制

        private Duration interval = Duration.ofSeconds(1);  // 重试间隔时间（初始值）

        private Integer multiplier = 1; // 重试间隔时间的倍增系数（指数退避策略）

        private Integer maxAttempts = 3;  // 最大重试次数

        private Boolean stateless = true; // 是否为无状态重试（true表示不保留重试上下文）
    }
}
