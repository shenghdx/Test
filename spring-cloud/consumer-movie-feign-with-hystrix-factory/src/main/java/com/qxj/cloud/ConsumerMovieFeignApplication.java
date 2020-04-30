package com.qxj.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

@SpringBootApplication
//该注解表明应用既作为eureka实例又为eureka client 可以发现注册的服务
@EnableEurekaClient
@EnableFeignClients
public class ConsumerMovieFeignApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ConsumerMovieFeignApplication.class, args);
	}
	
	/**
	 * 低版本直接启动即可使用 http://ip:port/hystrix.stream 查看监控信息
	 * 高版本需要添加本方法方可使用 http://ip:port/actuator/hystix.stream 查看监控信息
	 * 
	 * @return
	 */
	@Bean
	public ServletRegistrationBean<HystrixMetricsStreamServlet> getServlet() {
	    HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
	    ServletRegistrationBean<HystrixMetricsStreamServlet> registrationBean = new ServletRegistrationBean<HystrixMetricsStreamServlet>(streamServlet);
	    registrationBean.setLoadOnStartup(1);
	    registrationBean.addUrlMappings("/actuator/hystrix.stream");
	    registrationBean.setName("HystrixMetricsStreamServlet");
	    return registrationBean;
	}
}
