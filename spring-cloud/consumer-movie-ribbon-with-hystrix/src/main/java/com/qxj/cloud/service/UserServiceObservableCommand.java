package com.qxj.cloud.service;

import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import com.qxj.cloud.entity.User;

import rx.Observable;
import rx.Subscriber;

/**
 * 自定义HystrixObservableCommand实现
 * @author computer
 *
 */
public class UserServiceObservableCommand extends HystrixObservableCommand<User>{
	
	private RestTemplate restTemplate;
	private Long id;
	
	public UserServiceObservableCommand(String group,RestTemplate restTemplate,Long id) {
		super(HystrixCommandGroupKey.Factory.asKey(group));
		this.restTemplate=restTemplate;
		this.id = id;
	}

	@Override
	protected Observable<User> construct() {
		Observable<User> observable = Observable.unsafeCreate(new Observable.OnSubscribe<User>() {

			@Override
			public void call(Subscriber<? super User> subscriber) {
				if(!subscriber.isUnsubscribed()) {
					System.out.println("方法执行....");
					
					User user = restTemplate.getForObject("http://provider-user:7900/simple/" + id, User.class);
					
					//这个方法是监听方法，是传递结果的，请求多次的结果通过它返回去汇总起来。
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
	
	@Override
	protected Observable<User> resumeWithFallback() {
		Observable<User> observable = Observable.unsafeCreate(new Observable.OnSubscribe<User>() {
			@Override
			public void call(Subscriber<? super User> subscriber) {
				if (!subscriber.isUnsubscribed()) {
					User user = new User();
					user.setId(0L);
                    subscriber.onNext(user);
                    subscriber.onCompleted();
                }
			}
			
		});
		return observable;
	}
}
