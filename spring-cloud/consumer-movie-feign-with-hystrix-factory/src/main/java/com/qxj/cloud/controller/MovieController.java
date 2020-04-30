package com.qxj.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.qxj.cloud.entity.User;
import com.qxj.cloud.feign.UserFeignClient;

@RestController
public class MovieController {

	@Autowired
	private UserFeignClient userFeignClient;

	@RequestMapping(value="/movie/{id}",method = RequestMethod.GET,produces="application/json;charset=UTF-8")
	public User findById(@PathVariable Long id) {
		User user = this.userFeignClient.findById(id);
		return user;
	}
}
