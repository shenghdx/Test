package com.qxj.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
//该注解表明应用为eureka服务，有可以联合多个服务作为集群，对外提供服务注册以及发现功能
@EnableEurekaServer
public class EurekaHaApplication {
	public static void main(String[] args) {
		SpringApplication.run(EurekaHaApplication.class, args);
	}
}
