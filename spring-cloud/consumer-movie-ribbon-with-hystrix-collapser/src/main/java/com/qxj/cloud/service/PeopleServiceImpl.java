package com.qxj.cloud.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.qxj.cloud.entity.User;

@Service("peopleService")
public class PeopleServiceImpl implements PeopleService {

	@Autowired
	private RestTemplate restTemplate;

	//批量调用方法findAll，合并请求时间200ms
	@HystrixCollapser(batchMethod = "findAll", collapserProperties = {
			@HystrixProperty(name = "timerDelayInMilliseconds", value = "200") })
	public Future<User> find(Long id) {
		throw new RuntimeException("This method body should not be executed");
	}

	@HystrixCommand
	public List<User> findAll(List<Long> ids) {
		System.out.println(
				"Annotation---------" + ids + "Thread.currentThread().getName():" + Thread.currentThread().getName());
		User[] users = restTemplate.getForObject("http://provider-user:7900/users/{1}", User[].class,
				StringUtils.join(ids, ","));
		return Arrays.asList(users);
	}
	
}
