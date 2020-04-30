package com.qxj.cloud.service;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.qxj.cloud.entity.User;

@Service("userService")
public class UserServiceImpl implements UserService{
    @Autowired
    private RestTemplate restTemplate;
	
	@Override
	public User find(Long id) {
		return restTemplate.getForObject("http://provider-user:7900/users/{1}", User.class,id);
	}

	@Override
	public List<User> findAll(List<Long> ids) {
		System.out.println("finaAll request:---------" + ids + "Thread.currentThread().getName():-------" + Thread.currentThread().getName());
		User[] users = restTemplate.getForObject("http://provider-user:7900/users/{1}", User[].class,StringUtils.join(ids,","));
		return Arrays.asList(users);
	}
	
}
