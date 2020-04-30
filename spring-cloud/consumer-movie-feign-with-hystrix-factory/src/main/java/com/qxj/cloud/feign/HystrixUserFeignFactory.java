package com.qxj.cloud.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.qxj.cloud.entity.User;

import feign.hystrix.FallbackFactory;

@Component
public class HystrixUserFeignFactory implements FallbackFactory<UserFeignClient>{

	private static final Logger LOGGER= LoggerFactory.getLogger(HystrixUserFeignFactory.class);
	
	@Override
	public UserFeignClient create(Throwable cause) {
		//捕捉异常
		LOGGER.error("fallback; reason was: {}", cause.getMessage());
		cause.getMessage();
		//必须返回UserFeignClient（有@FeignClient的接口）的实现类
		return (id) -> {
			User user = new User();
			user.setId(-1L);
			return user;
		};
		
		//必须返回UserFeignClient（有@FeignClient的接口）的实现类
		/*return new UserFeignClient() {
			@Override
			public User findById(Long id) {
				User user = new User();
				user.setId(-1L);
				return user;
			}
			
		};*/
	}

}
