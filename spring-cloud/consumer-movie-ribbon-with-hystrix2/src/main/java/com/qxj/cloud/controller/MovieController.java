package com.qxj.cloud.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import com.qxj.cloud.entity.User;
import com.qxj.cloud.service.UserServiceCommand;
import com.qxj.cloud.service.UserServiceObservableCommand;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

@RestController
public class MovieController {

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 注解方式使用
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/movie/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@HystrixCommand(fallbackMethod = "findByIdFallback")
	public User findById(@PathVariable Long id) {
		// 微服务的虚拟id http://provider-user
		User user = this.restTemplate.getForObject("http://provider-user:7900/simple/" + id, User.class);
		return user;
	}

	// findById的fallback方法
	public User findByIdFallback(Long id) {
		User user = new User();
		user.setId(0L);
		return user;
	}

	/**
	 * 自定义实现类使用HystrixCommand
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/movieHystrixDiy/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public User findByIdDiy(@PathVariable Long id) {
		UserServiceCommand command = new UserServiceCommand("findByIdDiy", restTemplate, id);
		User user = command.execute();
		return user;
	}

	/**************** 非阻塞式IO ************************/

	/**
	 * 非阻塞式IO 自定义实现类使用HystrixCommand
	 * 
	 * @param id
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@RequestMapping(value = "/movieHystrixDiyFuture/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public User findByIdDiyFuture(@PathVariable Long id) throws InterruptedException, ExecutionException {
		UserServiceCommand command = new UserServiceCommand("findByIdDiyFuture", restTemplate, id);
		Future<User> future = command.queue();
		return future.get();
	}

	/**
	 * 非阻塞式IO 注解方式使用
	 * 
	 * @param id
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@RequestMapping(value = "/movieHystrixFuture/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@HystrixCommand(fallbackMethod = "findByIdFallback")
	public User findByIdFuture(@PathVariable Long id) throws InterruptedException, ExecutionException {

		Future<User> future = new AsyncResult<User>() {

			@Override
			public User get() {
				return invoke();
			}

			@Override
			public User invoke() {
				System.out.println("-------外部访问");
				return restTemplate.getForObject("http://provider-user:7900/simple/" + id, User.class);
			}
		};

		User user = future.get();
		System.out.println("-------执行");
		// 微服务的虚拟id http://provider-user
		return user;
	}

	/*************** 请求多个服务 *******************/

	/**
	 * 请求多个服务
	 * 自定义实现类使用HystrixObservableCommand
	 * 
	 * @param id
	 * @return
	 * @throws InterruptedException
	 */
	@RequestMapping(value = "/movieHystrixDiyMreq/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public List<User> findByIdDiyMreq(@PathVariable Long id) throws InterruptedException{
		//结果集
		List<User> list = new ArrayList<User>();
		
		UserServiceObservableCommand command = new UserServiceObservableCommand("findByIdDiyMreq", restTemplate, id);
		//热执行  不等待 事件注册完(onCompleted()，onError，onNext这三个事件注册)执行业务代码construct方法
		//Observable<User> observable = command.observe();
		//冷执行  等待 事件注册完(onCompleted()，onError，onNext这三个事件注册)才执行业务代码call方法
		Observable<User> observable = command.toObservable();
		//订阅
		observable.subscribe(new Observer<User>() {
			
			//请求完成的方法
			@Override
			public void onCompleted() {
				System.out.println("会聚完了所有查询请求");
			}

			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}
			
			//订阅调用事件，结果会聚的地方，用集合去装返回的结果会聚起来。
			@Override
			public void onNext(User t) {
				System.out.println("结果来了.....");
                list.add(t);
			}
		});
		return list;
	}
	
	/**
	 * 请求多个服务
	 * 注解方式实现类使用HystrixObservableCommand
	 * ObservableExecutionMode.EAGER热执行  ObservableExecutionMode.LAZY冷执行
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/movieHystrixMreq/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@HystrixCommand(fallbackMethod = "findByIdFallback",observableExecutionMode = ObservableExecutionMode.LAZY)
	public Observable<User> findByIdMreq(@PathVariable Long id) throws InterruptedException{
		Observable<User> observable = Observable.unsafeCreate(new Observable.OnSubscribe<User>() {
			@Override
			public void call(Subscriber<? super User> subscriber) {
				if(!subscriber.isUnsubscribed()) {
					System.out.println("方法执行....");
					
					User user = restTemplate.getForObject("http://provider-user:7900/simple/" + id, User.class);
					
					//这个方法监听方法，是传递结果的，请求多次的结果通过它返回去汇总起来。
					subscriber.onNext(user);
					
					User user2 = restTemplate.getForObject("http://provider-user:7900/simple/" + id, User.class);
					//这个方法是监听方法，传递结果的
					subscriber.onNext(user2);
					
					subscriber.onCompleted();
				}
			}
		});
		return observable;
	}
}
