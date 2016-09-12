package com.cjw.seckill.enums;

/**
 * 使用枚举表述常量数据字段
 * @author CJW
 *
 */
public enum SeckillStateEnum {
	SUCCESS(1,"秒杀成功"),
	END(0,"秒杀结束"),
	REPEAT_KILL(-1,"重复秒杀"),
	INNER_ERROR(-2,"系统异常"),
	DATA_REWRITE(-3,"数据篡改");
	
	//执行秒杀结果状态
	private int state;
	
	//状态表示
	private String stateInfo;
	
	

	private SeckillStateEnum(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}


	public static SeckillStateEnum stateOf(int index){
		for(SeckillStateEnum stateEnum : values()){
			if (stateEnum.getState() == index) {
				return stateEnum;
			}
		}
		return null;
	}
	
}
