package com.cjw.seckill.service;
import java.util.List;
import com.cjw.seckill.dto.Exposer;
import com.cjw.seckill.dto.SeckillExection;
import com.cjw.seckill.entity.Seckill;
import com.cjw.seckill.exception.RepeatKillException;
import com.cjw.seckill.exception.SeckillExceptionClose;
import com.cjw.seckill.exception.SeckillException;
/**
 * 业务接口：站在“使用者”角度设置接口
 * 三个方面:方法定义丽都，参数，返回类型（return 类型/异常）
 * @author CJW
 *
 */

public interface SeckillService{
	/**
	 * 查询所有秒杀记录
	 * @return
	 */
	List<Seckill> getSeckillList();
	/**
	 * 查询单个秒杀记录
	 * @return
	 */
	Seckill getSeckillById(long seckillId);
	/**
	 * 秒杀开启时输出秒杀接口地址
	 * 否则输出系统时间和秒杀时间
	 * @param seckillId
	 */
	Exposer exportSeckillUrl(long seckillId);
	/**
	 * 执行秒杀操作
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 */
	SeckillExection executeSeckill(long seckillId,long userPhone,String md5) throws RepeatKillException,SeckillExceptionClose,SeckillException;
	/**
	 * 执行秒杀存储过程
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 */
	SeckillExection executeSeckillProcedure(long seckillId,long userPhone,String md5);
}


