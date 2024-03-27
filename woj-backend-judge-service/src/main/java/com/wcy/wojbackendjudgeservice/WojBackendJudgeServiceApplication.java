package com.wcy.wojbackendjudgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.wcy")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.wcy.wojbackendserviceclient.service")
public class WojBackendJudgeServiceApplication {

    public static void main(String[] args) {
//        InitRabbitMq.doInit();
        SpringApplication.run(WojBackendJudgeServiceApplication.class, args);
    }

}
