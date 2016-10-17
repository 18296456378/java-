package com.cjw.seckill.redis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import redis.clients.jedis.Jedis;

public class RedisTest {
	public static void main(String[] args) {
		final String watchkeys = "watchkeys";
		ExecutorService executorService = Executors.newFixedThreadPool(20);
		executorService.execute(new MyRunnable());
		
		final Jedis jedis = new Jedis("127.0.0.1",6379);
		jedis.set(watchkeys, "0");//重置watchkeys为0
		jedis.del("setsucc","setfail");//删除抢购成功的和抢购不成功的
		jedis.close();
		//模拟一万人同时抢购
		for (int i = 0; i < 10; i++) {
			executorService.execute(new MyRunnable());
		}
		executorService.shutdown();//关闭exector
		
	}
}
