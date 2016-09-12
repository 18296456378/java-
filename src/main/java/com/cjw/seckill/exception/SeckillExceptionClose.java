package com.cjw.seckill.exception;
/**
 * 秒杀关闭异常
 * @author CJW
 *
 */
public class SeckillExceptionClose extends SeckillException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public SeckillExceptionClose(String message, Throwable cause) {
		super(message, cause);
	}

	public SeckillExceptionClose(String message) {
		super(message);
	}


	
}
