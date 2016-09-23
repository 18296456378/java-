package com.cjw.seckill.service;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.cjw.seckill.dto.Exposer;
import com.cjw.seckill.dto.SeckillExection;
import com.cjw.seckill.entity.Seckill;
import com.cjw.seckill.exception.RepeatKillException;
import com.cjw.seckill.exception.SeckillExceptionClose;
@ContextConfiguration(
		{"classpath:spring/spring-dao.xml",
		 "classpath:spring/spring-service.xml"
		})
@RunWith(SpringJUnit4ClassRunner.class)
public class SeckillServiceTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SeckillService seckillService;
	
	@Test
	public void testGetSeckillList() {
		List<Seckill> seckillList = seckillService.getSeckillList();
		logger.info("list={}",seckillList);
	}

	@Test
	public void testGetSeckillById() {
		Seckill seckillById = seckillService.getSeckillById(1001);
		logger.info("seckill{}",seckillById);
	}
	/**
	 * 测试秒杀购买内容
	 */
	@Test
	public void testExportSeckil() {
		long id = 1001;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		//判断秒杀是否开启
		if (exposer.isExposer()) {
			logger.info("exposer{}",exposer);
			long userPhone = 18296456784L;
			try {
				String md5 = exposer.getMd5();
				SeckillExection exection = seckillService.executeSeckill(id, userPhone, md5);
				logger.info("result={}", exection);
			}catch(RepeatKillException r){
				logger.error(r.getMessage());
			}catch (SeckillExceptionClose e) {
				logger.error(e.getMessage());
			}
		}else{
			//秒杀未开启
			logger.warn("exposer={}",exposer);
		}
		
	}
}
