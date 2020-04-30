package com.qxj.cloud.feign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import feign.Feign;

public class FeignDisableHystrixConfiguration {
    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }
}
