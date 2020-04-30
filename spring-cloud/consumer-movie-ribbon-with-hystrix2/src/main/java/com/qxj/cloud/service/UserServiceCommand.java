package com.qxj.cloud.service;

import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.qxj.cloud.entity.User;

/**
 * 自定义HystrixCommand实现
 * @author computer
 *
 */
public class UserServiceCommand extends HystrixCommand<User>{
	
	private String group;
	private RestTemplate restTemplate;
	private Long id;
	
	//自定义构造函数
	public UserServiceCommand(String group,RestTemplate restTemplate,Long id) {
		super(HystrixCommandGroupKey.Factory.asKey(group));
		this.group = group;
		this.restTemplate = restTemplate;
		this.id = id;
	}

	@Override
	protected User run() throws Exception {
		System.out.println(Thread.currentThread().getName());
		return this.restTemplate.getForObject("http://provider-user:7900/simple/" + id, User.class);
	}

	@Override
	protected User getFallback() {
		User user = new User();
		user.setId(0L);
		return user;
	}

	@Override
	protected String getCacheKey() {
		// TODO Auto-generated method stub
		System.out.println("cacheKey:"+this.group);
		return this.group;
	}
	
}
