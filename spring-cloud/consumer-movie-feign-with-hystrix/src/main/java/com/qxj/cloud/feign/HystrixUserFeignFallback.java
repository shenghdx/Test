package com.qxj.cloud.feign;

import org.springframework.stereotype.Component;

import com.qxj.cloud.entity.User;

//@Component注解将Hystrix类交给spring管理
@Component
public class HystrixUserFeignFallback implements UserFeignClient{

	@Override
	public User findById(Long id) {
		User user = new User();
		user.setId(0L);
		return user;
	}

}
