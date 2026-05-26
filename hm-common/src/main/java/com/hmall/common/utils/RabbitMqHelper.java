package com.hmall.common.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnClass(RabbitTemplate.class)
public class RabbitMqHelper {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routingKey, Object message) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            log.debug("消息发送成功，exchange: {}, routingKey: {}", exchange, routingKey);
        } catch (AmqpException e) {
            log.error("消息发送失败，exchange: {}, routingKey: {}, 原因: {}", exchange, routingKey, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String exchange, String routingKey, Object message, Integer delayMills) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message,
                    new DelayMessagePostProcessor(delayMills));
            log.debug("延迟消息发送成功，exchange: {}, routingKey: {}, delay: {}ms", exchange, routingKey, delayMills);
        } catch (AmqpException e) {
            log.error("延迟消息发送失败，exchange: {}, routingKey: {}, 原因: {}", exchange, routingKey, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public <T> T sendAndReceive(String exchange, String routingKey, Object message, Class<T> responseType) {
        try {
            T result = (T) rabbitTemplate.convertSendAndReceive(exchange, routingKey, message);
            log.debug("RPC消息发送成功，exchange: {}, routingKey: {}", exchange, routingKey);
            return result;
        } catch (AmqpException e) {
            log.error("RPC消息发送失败，exchange: {}, routingKey: {}, 原因: {}", exchange, routingKey, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }



    private static class DelayMessagePostProcessor implements MessagePostProcessor {
        private final Integer delayMills;

        public DelayMessagePostProcessor(Integer delayMills) {
            this.delayMills = delayMills;
        }

        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            message.getMessageProperties().setDelay(delayMills);
            return message;
        }
    }
}
