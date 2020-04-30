package com.qxj.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

//Springboot启动入口类
@SpringBootApplication
//eureka客户端注解
@EnableEurekaClient
public class SimpleProviderUserApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SimpleProviderUserApplication.class, args);
	}
}
