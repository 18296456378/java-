package com.cjw.seckill.dao;

import java.util.Date;
import java.util.List;

import com.cjw.seckill.entity.Seckill;

public interface SeckillDao {
	/**
	 * 减库存
	 * @param seckillId
	 * @param killTime
	 * @return 影响的行数
	 */
	int reduceNmuber(long seckillId,Date killTime);
	
	/**
	 * 根据秒杀id查询秒杀对象
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(long seckillId);
	/**
	 * 根据偏移量查询秒杀商品列表
	 * @param offet 偏移量
	 * @param limit 多少条
	 * @return
	 */
	List<Seckill> queryAll(int offet,int limit);
	

}
