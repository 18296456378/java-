package com.cjw.seckill.dao;

import com.cjw.seckill.entity.SuccessKilled;

public interface SuccessKilledDao {
	/**
	 * 插入购买明细
	 * @param seckillId
	 * @param userPhone
	 * @return 插入的行数
	 */
	int insertSuccessKilled(long seckillId,long userPhone);
	
	/**
	 * 根据id查询successKilled并携带秒杀对象
	 * @param seckillId
	 * @return
	 */
	SuccessKilled queryByIdWithSeckill(long seckillId);
}
