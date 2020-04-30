package com.qxj.cloud.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qxj.cloud.entity.User;

//fallback属性存在时，fallbackFactory会失效
@FeignClient(name="provider-user",/*fallback = HystrixUserFeignFallback.class,*/fallbackFactory = HystrixUserFeignFactory.class)
public interface UserFeignClient {
	@RequestMapping(value="/simple/{id}",method=RequestMethod.GET)
	public User findById(@PathVariable("id") Long id); //// 两个坑：1. @GetMapping不支持   2. @PathVariable得设置value
}
