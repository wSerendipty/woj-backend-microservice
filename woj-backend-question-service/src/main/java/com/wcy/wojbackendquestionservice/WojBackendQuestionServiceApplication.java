package com.wcy.wojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.wcy.wojbackendquestionservice.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.wcy")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.wcy.wojbackendserviceclient.service")
public class WojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WojBackendQuestionServiceApplication.class, args);
    }

}
