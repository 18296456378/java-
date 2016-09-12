package com.cjw.seckill.service.impl;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class SeckillServiceImpl implements SeckillService{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private SeckillDao seckillDao;
	
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
		if (now.getTime() <= startTime.getTime() || endTime.getTime() >= now.getTime()) {
			return new Exposer(false, now.getTime(), startTime.getTime(), endTime.getTime());
		}
		//转化特定的字符串
		String md5 = getMD5(seckillId);//TODO
		return new Exposer(true, md5, seckillId);
	}

	@Override
	public SeckillExection executeSeckill(long seckillId, long userPhone, String md5)
			throws RepeatKillException, SeckillExceptionClose, SeckillException {
		if (md5==null || md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		//执行秒杀逻辑：减库存 + 记录购买行为
		Date nowDate = new Date();
		try {
			//减库存
			int reduceNmuber = seckillDao.reduceNmuber(seckillId, nowDate);
			if (reduceNmuber<0) {
				//没有更新库存，秒杀结束
				throw new SeckillExceptionClose("seckill is closed");
			}else{
				//记录购买行为
				int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
				//表示重复购买
				if (insertCount<=0) {
					throw new RepeatKillException("seckill is repeat");
				}else{
					SuccessKilled queryByIdWithSeckill = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExection(seckillId,SeckillStateEnum.SUCCESS, "秒杀成功",queryByIdWithSeckill);
				}
			}
		}catch(SeckillExceptionClose e1){
			throw e1;
		}catch(RepeatKillException e2){
			throw e2;
		} catch (Exception e) {
			logger.error(e.getMessage());
			//将所有的编译期异常转换成运行期异常
			throw new SeckillException("seckill inner error :"+e.getMessage());
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

}
