package com.cjw.seckill.dao;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cjw.seckill.entity.Seckill;
/**
 * 配置spring和junitt整合,juint启动时加载springIOC容器
 * @author CJW
 * spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SeckillDaoTest {
	//注入Dao实现类的依赖
	@Autowired
	SeckillDao seckillDao;
	@Test
	public void testReduceNmuber() {
		int reduceNmuber = seckillDao.reduceNmuber(1000, new Date());
		System.err.println(reduceNmuber);
	}

	@Test
	public void testQueryById() {
		long id= 1000;
		Seckill queryById = seckillDao.queryById(id);
		System.out.println(queryById);
	}

	@Test
	public void testQueryAll() {
		List<Seckill> queryAll = seckillDao.queryAll(0, 10);
		for (Seckill seckill : queryAll) {
			System.out.println(seckill);
		}
	}

}
