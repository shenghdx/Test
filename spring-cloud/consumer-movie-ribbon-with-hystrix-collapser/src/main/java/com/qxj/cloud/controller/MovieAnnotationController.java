package com.qxj.cloud.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.qxj.cloud.entity.User;
import com.qxj.cloud.service.PeopleService;

@RestController
public class MovieAnnotationController {

	@Autowired
	private PeopleService peopleService;
	
	@RequestMapping(value = "/AnnoCollapseTest", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
	public List<User> requestCollapseTest() {
		List<Long> ids = new ArrayList<Long>();
		ids.add(1L);
		ids.add(2L);
		ids.add(3L);
		ids.add(4L);
		ids.add(5L);
		List<User> users = peopleService.findAll(ids);
		return users;
	}
	
    @RequestMapping(value = "/AnnoCollapse", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public List<User> requestCollapse() {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
    	List<User> list = new ArrayList<User>();
    	try {
    		Future<User> f1 = peopleService.find(1L);
    		Future<User> f2 = peopleService.find(2L);
    		Future<User> f3 = peopleService.find(3L);

            Thread.sleep(3000);

            Future<User> f4 = peopleService.find(4L);
            Future<User> f5 = peopleService.find(5L);
            
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
