package com.cjw.seckill.dto;
/**
 * 用来封装执行秒杀之后的结果
 * @author CJW
 *
 */
import com.cjw.seckill.entity.SuccessKilled;
import com.cjw.seckill.enums.SeckillStateEnum;
public class SeckillExection{ 
	
	private long seckillId;
	
	//执行秒杀结果的状态
	private int state;
	
	//状态
	private String stateInfo;
	
	//秒杀成功返回对象
	private SuccessKilled successKilled;
	
	
	
	public SeckillExection(long seckillId, SeckillStateEnum stateEnum, String stateInfo, SuccessKilled successKilled) {
		super();
		this.seckillId = seckillId;
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.successKilled = successKilled;
	}
	
	

	public SeckillExection(long seckillId, SeckillStateEnum stateEnum) {
		super();
		this.seckillId = seckillId;
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}



	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}

	public long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public SuccessKilled getSuccessKilled() {
		return successKilled;
	}

	public void setSuccessKilled(SuccessKilled successKilled) {
		this.successKilled = successKilled;
	}
	
	
	
}
