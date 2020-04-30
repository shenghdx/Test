package com.qxj.cloud.command;

import java.util.ArrayList;
import java.util.List;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.qxj.cloud.entity.User;
import com.qxj.cloud.service.UserService;

/**
 * 自定义HystrixCommand实现
 * @author computer
 *
 */
public class UserBatchCommand extends HystrixCommand<List<User>>{
	
	private UserService userService;
	private List<Long> ids;
	
	//自定义构造函数
	public UserBatchCommand(UserService userService,List<Long> userIds) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")).
                andCommandKey(HystrixCommandKey.Factory.asKey("GetValueForKey")));
		this.userService = userService;
		this.ids = userIds;
	}

	@Override
	protected List<User> run() throws Exception {
		return this.userService.findAll(ids);
	}

	@Override
	protected List<User> getFallback() {
		List<User> list = new ArrayList<User>();
		User user = new User();
		user.setId(0L);
		list.add(user);
		return list;
	}
	
}
