package com.cjw.seckill.dto;
/**
 * 数据传输层(方便service返回数据的封装)
 * 暴露秒杀接口地址DTO
 * @author CJW
 *
 */
public class Exposer {
	//是否暴露秒杀接口
	private boolean exposer;
	
	//一种加密措施
	private String md5;
	
	//系统当前时间(单位：毫秒)
	private long now;
	
	//开启时间
	private long startTime;
	
	//结束时间
	private long endTime;
	
	//id
	private long seckillId;

	public Exposer(boolean exposer, String md5, long seckillId) {
		super();
		this.exposer = exposer;
		this.md5 = md5;
		this.seckillId = seckillId;
	}

	public Exposer(boolean exposer, long now, long startTime, long endTime) {
		super();
		this.exposer = exposer;
		this.now = now;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Exposer(boolean exposer, long seckillId) {
		super();
		this.exposer = exposer;
		this.seckillId = seckillId;
	}

	public boolean isExposer() {
		return exposer;
	}

	public void setExposer(boolean exposer) {
		this.exposer = exposer;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public long getNow() {
		return now;
	}

	public void setNow(long now) {
		this.now = now;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}

	@Override
	public String toString() {
		return "Exposer [exposer=" + exposer + ", md5=" + md5 + ", now=" + now + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", seckillId=" + seckillId + "]";
	}
	
	
	
	
	
	
	
}
