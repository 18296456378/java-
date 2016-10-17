package com.cjw.seckill.redis;
import java.util.List;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
/**
 * redis使用watch实现秒杀功能:用于监视一个(或多个) key ，
 * 如果在事务执行之前这个(或这些) key 被其他命令所改动，那么事务将被打断
 * 1. 首先选用内存数据库来抢购速度极快。
   2. 速度快并发自然没不是问题。
   3. 使用悲观锁，会迅速增加系统资源。
   4. 比队列强的多，队列会使你的内存数据库资源瞬间爆棚。
   5. 使用乐观锁，达到综合需求
 * @author CJW
 *
 */
public class MyRunnable implements Runnable{
	String watchkeys = "watchkeys";//监视keys
	Jedis jedis = new Jedis("127.0.0.1",6379);
	public MyRunnable() {
	}
	
	@Override
	public void run() {
		jedis.watch(watchkeys);
		String val = jedis.get(watchkeys);
		Integer valint = Integer.valueOf(val);
		String userinfo = UUID.randomUUID().toString();//获取uuid
		if (valint < 10) {
			Transaction tx = jedis.multi();//开启事物
			tx.incr(watchkeys);/* 用于由一个递增key的整数值。如果该key不存在，它被设置为0执行操作之前。
								  	如果key包含了错误类型的值或包含不能被表示为整数，字符串，则返回错误。
									该操作被限制为64位带符号整数。
									*/
			
			List<Object> list = tx.exec();//提交事物如果此时watchkeys被改动则返回null
			if (list != null) {
				System.out.println("用户："+userinfo+"抢购成功，当前成功抢购人数："+(valint+1));
				//成功业务抢购逻辑
				 jedis.sadd("setsucc", userinfo);
			}else{
				System.out.println("用户："+userinfo+"抢购失败");
				jedis.sadd("setfail", userinfo);
			}
		}else{
			System.out.println("用户："+userinfo+"抢购失败");
			jedis.sadd("setfail", userinfo);
			//Thread.sleep(500);
			return;
		}
		jedis.close();
		
	}
	

}
