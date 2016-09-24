-- 数据库初始化脚本

-- 创建数据库
DROP DATABASE SECKILL;
CREATE DATABASE seckill;
-- 使用数据库
use seckill;
-- 创建秒杀库存表
DROP TABLE IF EXISTS seckill;
CREATE TABLE seckill(
	seckill_id bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
	name VARCHAR(120) NOT NULL COMMENT '商品名称',
	number INT NOT NULL COMMENT '库存数',
	start_time timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始秒杀时间',
	end_time timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束秒杀时间',
	create_time timestamp NOT NULL DEFAULT  NOW() ON UPDATE NOW() COMMENT '创建时间',
	PRIMARY KEY(seckill_id),
	key index_start_time(start_time), /**索引（加速查询）**/
	key index_end_time(end_time),
	key index_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=UTF8 COMMENT='秒杀库存表';

--初始化数据
insert into 
	seckill(name,number,start_time,end_time)
values('1000元秒杀iPhone7',100,'2016-09-08 00:00:00','2016-09-09 00:00:00'),
	  ('500元秒杀小米5',100,'2016-09-08 00:00:00','2016-09-09 00:00:00'),
	  ('200元秒杀iPadAir',100,'2016-09-08 00:00:00','2016-09-09 00:00:00'),
	  ('1000元秒杀华硕电脑',100,'2016-09-08 00:00:00','2016-09-09 00:00:00'),
	  ('600元秒杀红米note4',100,'2016-09-08 00:00:00','2016-09-09 00:00:00'),
	  ('888元秒杀iPhone6是',100,'2016-09-08 00:00:00','2016-09-09 00:00:00');

-- 秒杀成功明细表
DROP TABLE IF EXISTS success_killed;
CREATE TABLE success_killed(
	seckill_id BIGINT  NOT NULL COMMENT '商品id',
	user_phone BIGINT NOT NULL COMMENT '用户手机',
	state tinyint NOT NULL DEFAULT -1 COMMENT '状态标识：-1标识无效、0标识成功、1已付款', /**tinyint mysql最小的int类型**/
	create_time timestamp NOT NULL DEFAULT  NOW() ON UPDATE NOW() COMMENT '创建时间',
	PRIMARY KEY(seckill_id,user_phone), -- 联合主键
	KEY index_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT = '秒杀成功明细表';
--连接数据库控制台


-- 为什么要手写DDL
-- 记录每次上线的DDL修改
-- 上线v1.1
ALTER TABLE seckill
DROP INDEX index_start_time, -- 删除索引
ADD INDEX index_c_s(start_time,create_time);

-- 上线v1.2
-- DDL

-- 创建秒杀存储过程
-- 秒杀执行存储过程
DELIMITER $$ -- console;转换$$为
-- 定义存储过程
-- 参数：in 输入参数；out输出参数
-- ROW_COUNT();用来返回上一条修改类型sql(DELETE,INSERT,UPDATE)的影响行数
-- ROW_COUNT():0表示未修改数据>0：表示修改的行数<0:表示程序出错/未执行sql
CREATE PROCEDURE execute_seckill
	(IN v_seckill_id BIGINT,IN v_phone BIGINT,
		IN v_kill_time TIMESTAMP,OUT r_result INT)
BEGIN
	DECLARE insert_count INT DEFAULT 0; -- 定义一个变量
	START TRANSACTION; -- 开启事物
	INSERT IGNORE INTO success_killed
		(seckill_id,user_phone,create_time)
	VALUES(v_seckill_id,v_phone,v_kill_time);
	SELECT ROW_COUNT() INTO insert_count;
	IF(insert_count = 0) THEN
		ROLLBACK;
		SET r_result = -1; -- 代表重复秒杀
	ELSEIF(insert_count < 0)THEN
		ROLLBACK;
		SET r_result = -2; -- 代表系统异常
	ELSE
		UPDATE seckill
			SET number = number-1
			WHERE seckill_id = v_seckill_id
				AND v_kill_time > start_time
				AND v_kill_time < end_time
				AND number > 0;
		SELECT ROW_COUNT() INTO insert_count;
		IF(insert_count = 0)THEN
			SET r_result = 0;
			ROLLBACK;
		ELSEIF(insert_count < 0)THEN
			SET r_result = -2; -- 系统异常
		ELSE
			SET r_result = 1;
			COMMIT;
		END IF;
	END IF;
END;
$$ -- 定义存储过程结束

DELIMITER ;
SET @r_result = -3;
-- 执行存储过程
CALL execute_seckill(1001,18296456379,NOW(),@r_result);
-- 获取结果
SELECT @r_result;

-- 存储过程
-- 1:存储过程优化：减少事物行级锁持有时间
-- 2:不要过度依赖存储过程
-- 3：简单的逻辑可以应用存储过程
-- 4：QPS:一个秒杀单6000/qps(查询吞吐量)