package com.qxj.cloud.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.qxj.cloud.command.UserCollapseCommand;
import com.qxj.cloud.entity.User;
import com.qxj.cloud.service.UserService;

@RestController
public class MovieController {

	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/collapseTest", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
	public List<User> requestCollapseTest() {
		List<Long> ids = new ArrayList<Long>();
		ids.add(1L);
		ids.add(2L);
		ids.add(3L);
		ids.add(4L);
		ids.add(5L);
		List<User> users = userService.findAll(ids);
		return users;
	}
	
    @RequestMapping(value = "/collapse", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public List<User> requestCollapse() {
    	//启用Hystrix缓存
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
    	List<User> list = new ArrayList<User>();
    	try {
        	String collapserKey = "userCollapseCommand";
            Future<User> f1 = new UserCollapseCommand(collapserKey,userService, 1L).queue();
            Future<User> f2 = new UserCollapseCommand(collapserKey,userService, 2L).queue();
            Future<User> f3 = new UserCollapseCommand(collapserKey,userService, 3L).queue();
            
            Thread.sleep(3000);
            
            Future<User> f4 = new UserCollapseCommand(collapserKey,userService, 4L).queue();
            Future<User> f5 = new UserCollapseCommand(collapserKey,userService, 5L).queue();

            User u1 = f1.get();
            User u2 = f2.get();
            User u3 = f3.get();

            User u4 = f4.get();
            User u5 = f5.get();
            
            list.add(u1);
            list.add(u2);
            list.add(u3);
            list.add(u4);
            list.add(u5);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            context.close();
        }
    	return list;
    }
}
