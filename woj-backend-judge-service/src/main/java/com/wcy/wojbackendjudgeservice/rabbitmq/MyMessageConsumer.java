package com.wcy.wojbackendjudgeservice.rabbitmq;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.wcy.wojbackendjudgeservice.JudgeService;
import com.wcy.wojbackendmodel.model.judge.model.JudgeContextData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MyMessageConsumer {

    @Resource
    private JudgeService judgeService;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {"submit_queue"}, ackMode = "MANUAL")
    public void receiveSubmitMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        if (message == null || message.isEmpty()) {
            channel.basicNack(deliveryTag, false,false);
            return;
        }
        try {
            JudgeContextData judgeContextData = JSONUtil.toBean(message, JudgeContextData.class);
            judgeService.doJudge(judgeContextData);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {"run_queue"}, ackMode = "MANUAL")
    public void receiveRunMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        if (message == null || message.isEmpty()) {
            channel.basicNack(deliveryTag, false,false);
            return;
        }
        try {
            JudgeContextData judgeContextData = JSONUtil.toBean(message, JudgeContextData.class);
            judgeService.runJudge(judgeContextData);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error(e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

}