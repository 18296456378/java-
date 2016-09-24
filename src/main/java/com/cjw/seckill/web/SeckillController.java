package com.cjw.seckill.web;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cjw.seckill.dto.Exposer;
import com.cjw.seckill.dto.SeckillExection;
import com.cjw.seckill.dto.SeckillResult;
import com.cjw.seckill.entity.Seckill;
import com.cjw.seckill.enums.SeckillStateEnum;
import com.cjw.seckill.exception.RepeatKillException;
import com.cjw.seckill.exception.SeckillExceptionClose;
import com.cjw.seckill.service.SeckillService;

@Controller
@RequestMapping("/seckill") //url:/模块/资源/{id}/细分
public class SeckillController {
	@Autowired
	SeckillService seckillService;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 获取商品列表
	 * @param model
	 * @return
	 */
	@RequestMapping(value ="/list",method = RequestMethod.GET)
	public String list(Model model){
		List<Seckill> seckillList = seckillService.getSeckillList();
		model.addAttribute("list", seckillList);
		return "list";
	}
	/**
	 * 获取某个商品详情
	 * @param seckillId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId ,Model model){
		if (seckillId == null) {
			return "redirect:seckill/list";
		}
		Seckill seckill = seckillService.getSeckillById(seckillId);
		if (seckill == null) {
			return "forward:seckill/list";
		}
		model.addAttribute("seckill", seckill);
		return "detail";
	}
	/**
	 * 获取秒杀地址
	 * ajax 返回 json 数据格式
	 * @param seckillId
	 * @return
	 */
	@RequestMapping(value="/{seckillId}/exposer",
			method = RequestMethod.POST,
			produces = {"application/json;charset=UTF-8"})
	@ResponseBody//返回json格式数据
	public SeckillResult<Exposer> /*TODO*/ exposer(@PathVariable Long seckillId){//@PathVariable获得请求url中的动态参数
		SeckillResult<Exposer> seckillResult;
		try {
			Exposer export = seckillService.exportSeckillUrl(seckillId);
			seckillResult = new SeckillResult<Exposer>(true, export);
		} catch (Exception e) {
			logger.info(e.getMessage(),e);
			seckillResult = new SeckillResult<Exposer>(false, e.getMessage());
		}
		return seckillResult;
	}
	/**
	 * 执行秒杀操作
	 * @param seckillId
	 * @param md5
	 * @param phone
	 * @return
	 */
	@RequestMapping(
			value="/{seckillId}/{md5}/execute",
			method = RequestMethod.POST,
			produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExection> execute(@PathVariable("seckillId") Long seckillId,
												  @PathVariable("md5") String md5,
												  @CookieValue(value="killPhone",required = false) Long userPhone){
		if (userPhone == null) {
			return new SeckillResult<>(true, "未注册");
		}
		SeckillResult<SeckillExection> result;
		try {
			//存储过程调用
			SeckillExection seckillExection = seckillService.executeSeckillProcedure(seckillId, userPhone, md5);
			result = new SeckillResult<SeckillExection>(true, seckillExection);
			return result;
		} catch (RepeatKillException r){
			SeckillExection seckillExection = new SeckillExection(seckillId, SeckillStateEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExection>(true, seckillExection);
		}catch (SeckillExceptionClose s) {
			SeckillExection seckillExection = new SeckillExection(seckillId, SeckillStateEnum.END);
			return new SeckillResult<SeckillExection>(true, seckillExection);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			SeckillExection seckillExection = new SeckillExection(seckillId, SeckillStateEnum.INNER_ERROR);
			return new SeckillResult<SeckillExection>(true, seckillExection);
		}
	}
	/**
	 * 获取服务器当前时间
	 * @return
	 */
	@RequestMapping(value="/time/now",method = RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time(){
		Date now = new Date();
		return new SeckillResult<Long>(true, now.getTime());
	}
}
