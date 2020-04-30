package com.qxj.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
//该注解表明应用既作为eureka实例又为eureka client 可以发现注册的服务
@EnableEurekaClient
//在启动该微服务的时候就能去加载我们的自定义Ribbon配置类
@RibbonClient(name = "provider-user")
//Hystrix启动类注解,允许断路器
@EnableCircuitBreaker
public class ConsumerMovieRibbonApplication {
	
	@Bean
	//使用该注解可以用 http://provider-user:7900/simple/ 虚拟主机名  访问地址
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ConsumerMovieRibbonApplication.class, args);
	}
}
