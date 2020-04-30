package com.qxj.configuration;

import java.util.List;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

public class RandomRule_QXJ extends AbstractLoadBalancerRule{
	
	private int total = 0; 	    // 总共被调用的次数，目前要求每台被调用5次
	private int currentIndex = 0;	    // 当前提供服务的机器号
	
	/**
	 * 服务选择算法,要求每台被调用5次
	 * @param lb
	 * @param key
	 * @return
	 */
	public Server choose(ILoadBalancer lb,Object key) {
		if(lb == null) {
			return null;
		}
		
		Server server = null;
		while(server == null) {
			//判断当前线程是否中断
			//interrupted()是静态方法：内部实现是调用的当前线程的isInterrupted()，并且会重置当前线程的中断状态
			if(Thread.interrupted()) {
				return null;
			}
			//激活可用的服务
			List<Server> upList = lb.getReachableServers();
			//所有的服务
			List<Server> allList = lb.getAllServers();
			
			int serverCount = allList.size();
			if(serverCount == 0) {
				return null;
			}
			
			if(total < 5) {
				server = upList.get(currentIndex);
				total++;
			}else {
				total=0;
				//使用下一台机器服务
				currentIndex++;
				//若当前服务机器为upList集合里最后一台，重新使用第一台机器服务
				if(currentIndex >= upList.size()) {
					currentIndex=0;
				}
			}
			System.out.println("currentIndex:" + currentIndex +"---total:"+total);
			//循环到第一台服务时，server==null,需要重新获取server
			if(server == null) {
				Thread.yield();
				continue;
			}
			
			if(server.isAlive()) {
				return server;
			}
			
			//该代码实际上不会执行
			server = null;
			Thread.yield();
		}
		
		return server;
	}
	
	@Override
	public Server choose(Object key) {
		return choose(getLoadBalancer(),key);
	}

	@Override
	public void initWithNiwsConfig(IClientConfig clientConfig) {
	}

}
