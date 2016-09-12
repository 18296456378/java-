package com.cjw.seckill.dao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cjw.seckill.entity.SuccessKilled;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {
	@Autowired
	SuccessKilledDao successKilledDao;
	@Test
	public void testInsertSuccessKilled() {
		int insertSuccessKilled = successKilledDao.insertSuccessKilled(1001L, 182964656378L);
		System.out.println(insertSuccessKilled);
	}

	@Test
	public void testQueryByIdWithSeckill() {
		long id = 1001;
		long phone = 18296456378L;
		SuccessKilled queryByIdWithSeckill = successKilledDao.queryByIdWithSeckill(id,phone);
		System.out.println(queryByIdWithSeckill);
		System.out.println(queryByIdWithSeckill.getSeckill());
	}

}
