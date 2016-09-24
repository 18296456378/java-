package com.cjw.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import com.cjw.seckill.dao.SeckillDao;
import com.cjw.seckill.dao.SuccessKilledDao;
import com.cjw.seckill.dto.Exposer;
import com.cjw.seckill.dto.SeckillExection;
import com.cjw.seckill.entity.Seckill;
import com.cjw.seckill.entity.SuccessKilled;
import com.cjw.seckill.enums.SeckillStateEnum;
import com.cjw.seckill.exception.RepeatKillException;
import com.cjw.seckill.exception.SeckillException;
import com.cjw.seckill.exception.SeckillExceptionClose;
import com.cjw.seckill.service.SeckillService;
@Service //@Component(不知道什么时候就就用这个)
public class SeckillServiceImpl implements SeckillService{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillDao seckillDao;
	
	@Autowired
	private SuccessKilledDao successKilledDao;
	 
	//md5颜值字符串，用于混淆MD5
	private final String slat = "sdfkkls-1=-23jk123kksfa&*^%#45";
	
	@Override
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 10);
	}

	@Override
	public Seckill getSeckillById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		Seckill seckill = seckillDao.queryById(seckillId);
		if (seckill==null) {
			return new Exposer(false, seckillId);
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		//系统当前时间
		Date now = new Date();
		if (now.getTime() <= startTime.getTime() || endTime.getTime() <= now.getTime()) {
			return new Exposer(false, now.getTime(), startTime.getTime(), endTime.getTime());
		}
		//转化特定的字符串
		String md5 = getMD5(seckillId);//TODO
		return new Exposer(true, md5, seckillId);
	}
	@Override
	@Transactional
	/**
	 * 使用注解控制事物方法的优点：
	 * 1：开发团队达成一致协议，明确标注方法的编程
	 * 2：保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/redis
	 * 3：不是所有的方法都需要事务，如只有一条修改操作，只读操作
	 */
	public SeckillExection executeSeckill(long seckillId, long userPhone, String md5)
			throws RepeatKillException, SeckillExceptionClose, SeckillException {
		if (md5==null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		//执行秒杀逻辑：减库存 + 记录购买行为
		Date nowDate = new Date();
		try {
			//记录购买行为
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			//表示重复购买
			if (insertCount<=0) {
				throw new RepeatKillException("seckill is repeat");
			}else{
				//减库存
				int reduceNmuber = seckillDao.reduceNmuber(seckillId, nowDate);
				if (reduceNmuber<0) {
					//没有更新库存，秒杀结束	rollBack事物
					throw new SeckillExceptionClose("seckill is closed");
				}else{
					//秒杀成功	commit
					SuccessKilled queryByIdWithSeckill = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExection(seckillId,SeckillStateEnum.SUCCESS,queryByIdWithSeckill);
				}
			}
			
		}catch(SeckillExceptionClose e1){
			throw e1;
		}catch(RepeatKillException e2){
			throw e2;
		} catch (Exception e) {
			logger.error(e.getMessage());
			//将所有的编译期异常转换成运行期异常
			throw new SeckillException("seckill inner error :"+e.getMessage());//通过异常区告诉spring声明式事物是rollback还是commit
		}
	}
	
	/**
	 * 自定义md5加载
	 * @param seckillId
	 * @return
	 */
	private String getMD5(long seckillId){
		String base = seckillId + "/" + slat;
		String MD5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return MD5;
	}

	@Override
	public SeckillExection executeSeckillProcedure(long seckillId, long userPhone, String md5){
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			return new SeckillExection(seckillId, SeckillStateEnum.DATA_REWRITE);
		}
		Date killTime = new Date();
		Map<String, Object> map = new HashMap<>();
		map.put("seckillId", seckillId);
		map.put("userPhone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		//执行存储过程
		try {
			seckillDao.seckillProcedure(map);
			//获取result
			Integer reslut = MapUtils.getInteger(map, "result",-2);
			if (reslut==1) {
				SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExection(seckillId, SeckillStateEnum.SUCCESS, sk);
			}else{
				return new SeckillExection(seckillId, SeckillStateEnum.stateOf(reslut));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new SeckillExection(seckillId, SeckillStateEnum.INNER_ERROR);
		}
	}

}
