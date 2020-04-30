package com.qxj.cloud.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.qxj.cloud.entity.User;
import com.qxj.cloud.repository.UserRepository;

//RestController =  @Controller + @ResponseBody
@RestController
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
    private Registration registration; // 服务注册
	
	@Autowired
	private EurekaClient eurekaClient;
	
	//@GetMapping = @RequestMapping(value="/simple/{id}",method = RequestMethod.GET)
	//@GetMapping("/simple/{id}")
	//暂时使用produces设置编码，可以修改springmvs的json转换器设置编码格式
	@RequestMapping(value="/simple/{id}",method = RequestMethod.GET,produces="application/json;charset=UTF-8")
	public User findById(@PathVariable Long id) {
		Optional<User> optional = this.userRepository.findById(id);
		User user = optional.get();
		return user;
	}
	
	
    @RequestMapping(value = "/users/{ids}", method = RequestMethod.GET,produces="application/json;charset=UTF-8")
    public List<User> batchUser(@PathVariable("ids") String ids) {
        System.out.println("ids===:" + ids);
        
        List<Long> idList = strArrayConvertToLongList(ids);
        List<User> lists = new ArrayList<User>();
        
        for(int i=0;i<idList.size();i++) {
        	Optional<User> optional = this.userRepository.findById(idList.get(i));
    		User user = optional.get();
    		lists.add(user);
        }

        return lists;
    }
    
    /**
     * ids转List<Long>
     * @param ids
     * @return
     */
    private static List<Long> strArrayConvertToLongList(String ids){
    	String[] idArray = StringUtils.split(ids,",");
    	return Arrays.stream(idArray).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
    }
	
	
	@GetMapping("/eureka-instance")
	public String ServerUrl() {
		InstanceInfo instance = this.eurekaClient.getNextServerFromEureka("provider-user", false);
		return instance.getHomePageUrl();
	}
	
	@GetMapping("/instance-info")
	public List<ServiceInstance> showInfo() {
		List<ServiceInstance> localServiceInstance = this.discoveryClient.getInstances(registration.getServiceId());
		return localServiceInstance;
	}
	
	//@RequestBody请求参数类型为对象注解
	@PostMapping("/user")
	public User getUser(@RequestBody User user) {
		return user;
	}
}
