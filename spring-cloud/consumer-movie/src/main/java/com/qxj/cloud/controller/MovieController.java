package com.qxj.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.qxj.cloud.entity.User;

@RestController
public class MovieController {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${user.userServicePath}")
	private String userServicePath;

	//@GetMapping("/movie/{id}")
	@RequestMapping(value="/movie/{id}",method = RequestMethod.GET,produces="application/json;charset=UTF-8")
	public User findById(@PathVariable Long id) {
		User user = this.restTemplate.getForObject(this.userServicePath + id, User.class);
		return user;
	}

}
