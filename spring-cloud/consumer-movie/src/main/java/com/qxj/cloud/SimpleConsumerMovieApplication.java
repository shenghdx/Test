package com.qxj.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
//该注解表明应用既作为eureka实例又为eureka client 可以发现注册的服务
@EnableEurekaClient
public class SimpleConsumerMovieApplication {
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SimpleConsumerMovieApplication.class, args);
	}
}
