package com.flashkill.seckillbackend.mq.producer;

import com.flashkill.seckillbackend.config.RabbitMQConfig;
import com.flashkill.seckillbackend.mq.message.SeckillMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeckillMessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(SeckillMessage message) {
        log.info("发送秒杀消息：{}", message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SECKILL_EXCHANGE,
                RabbitMQConfig.SECKILL_ROUTING_KEY,
                message);
    }
}