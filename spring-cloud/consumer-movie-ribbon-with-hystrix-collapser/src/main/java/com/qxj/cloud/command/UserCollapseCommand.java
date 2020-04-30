package com.qxj.cloud.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;
import com.qxj.cloud.entity.User;
import com.qxj.cloud.service.UserService;

/**
 * 通过看HystrixCollapser类的源码: public abstract class
 * HystrixCollapser<BatchReturnType, ResponseType, RequestArgumentType>
 * 我们可以知道List<User>表示:合并后批量请求的返回类型
 * User表示：单个请求返回的类型
 * Long表示:请求参数类型
 * 
 * @author computer
 *
 */
public class UserCollapseCommand extends HystrixCollapser<List<User>, User, Long>{
	
    private UserService userService;

    private Long id;
    

    public UserCollapseCommand(String collapserKey,UserService userService, Long id) {
    	//接收Setter对象 设置key 和 合并请求时间100ms
        super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey(collapserKey)).
                andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(100)));
        this.userService = userService;
        this.id = id;
    }
	
	@Override
	public Long getRequestArgument() {
		// TODO Auto-generated method stub
		return id;
	}

	/**
     * @param collapsedRequests 保存了延迟时间窗中收集到的所有获取单个User的请求。通过获取这些请求的参数来组织
     *                          我们准备的批量请求命令UserBatchCommand实例
	 */
	@Override
	protected HystrixCommand<List<User>> createCommand(Collection<CollapsedRequest<User, Long>> requests) {
		List<Long> ids = new ArrayList<Long>(requests.size());
		//CollapsedRequest::getArgument 知识点  lambda表达式的一种简写
		//将请求查询参数id收集到ids集合里
		ids.addAll(requests.stream().map(CollapsedRequest::getArgument).collect(Collectors.toList()));
		return new UserBatchCommand(userService,ids);
	}

    /**
     * 在批量请求命令UserBatchCommand实例被触发执行完成后，该方法开始执行，
     * 在这里我们通过批量结果batchResponse对象，为collapsedRequests中每个合并前的单个请求设置返回结果。来完成批量结果到单个请求结果的转换
     * @param batchResponse 保存了createCommand中组织的批量请求命令的返回结果
     * @param collapsedRequests 代表了每个合并的请求
     */
	@Override
	protected void mapResponseToRequests(List<User> batchResponse, Collection<CollapsedRequest<User, Long>> requests) {
		System.out.println("mapResponseToRequests========>");
		int count = 0;
		//合并请求成功
		if(batchResponse.size() == requests.size()) {
			for(CollapsedRequest<User,Long> collapsedRequest:requests) {
				User user = batchResponse.get(count++);
				collapsedRequest.setResponse(user);
			}
		}else {
			//合并请求失败
			User user = batchResponse.get(count);
			for(CollapsedRequest<User,Long> collapsedRequest:requests) {
				collapsedRequest.setResponse(user);
			}
		}
	}

}
