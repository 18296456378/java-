//主要存放交互逻辑js代码
//javascript模块化
var seckill = {
	URL:{
		now:function(){
			return '/seckill/seckill/time/now';
		},
		exposer:function(seckillId){
			return '/seckill/seckill/'+seckillId+'/exposer';
		},
		executeUrl:function(seckillId,md5){
			return '/seckill/seckill/'+seckillId+'/'+md5+'/execute';
		}
	},
	validatePhone:function(phone){
		if (phone && phone.length == 11 && !isNaN(phone)) {
			return true;
		}else{
			return false;
		}
	},
	handleSeckillKill:function(seckillId,node){
		//获取秒杀地址、控制秒杀逻辑、执行秒杀
		node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>')
		$.post(seckill.URL.exposer(seckillId),{},function(result){
			if (result && result.success) {
				var exposer = result.data;
				if (exposer && exposer.exposer) {
					//显示按钮
					$('#killBtn').one('click',function(){//使按钮只能点击一次
						//执行秒杀请求
						//1.禁用按钮
						$(this).addClass('disabled');
						//2.发送秒杀请求
						var md5 = exposer.md5;
						$.post(seckill.URL.executeUrl(seckillId, md5),{},function(result){
							if (result && result.success) {
								var killResult = result.data;
								var state = killResult.state;
								var stateInfo = killResult.stateInfo;
								//3.执行秒杀结果
								node.html('<label class="label label-success">'+stateInfo+'</label>');
								console.log('reslut:'+result);
							}
						});
					});
					node.show();
				}else {//未开启秒杀(主要是服务器时间和本地时间不相同)
					var startTime = exposer.startTime;
					var endTime = exposer.endTime;
					var nowTime = exposer.now;
					seckill.countDown(seckillId, startTime, endTime, nowTime);//重新开启倒计时
				}
				
			}else{
				console.log('reslut:'+result);
			}
		});
	},
	countDown:function(seckillId,startTime,endTime,nowTime){
		var seckillBox = $('#seckill-box');
		//时间判断
		if (nowTime < startTime) {
			//秒杀未开启，计时绑定
			var killTime = new Date(Number(startTime));
			
			seckillBox.countdown(killTime,function(event){
				//时间格式 
				var format = event.strftime('秒杀倒计时：%D天  %H时  %M分  %S秒');
				seckillBox.html(format);
			}).on('finish.countdown',function(){
				//计时完成调用
				seckill.handleKill(seckillId);
			});
		}else if (nowTime > endTime) {
			seckillBox.html('秒杀已结束');
		}else {
			seckill.handleSeckillKill(seckillId,seckillBox);
		}
	},
	//详情页秒杀逻辑
	detail:{
		//详情页面初始化
		init : function(params){
			//手机验证和登录，设计交互
			//规划我们的交互流程
			//在cookie中查找手机号
			var killPhone = $.cookie('killPhone');
			//验证手机号
			if (!seckill.validatePhone(killPhone)) {
				//绑定phone
				var killPhoneModal = $('#killPhoneModal');
				killPhoneModal.modal({
					show: true, //显示弹出层
					backdrop:'static',//关闭点击其他区域关闭事件
					keyboard:false //关闭esc关闭键盘事件
				});
				$('#killPhoneBtn').click(function(){
					var inputPhone = $('#killPhoneKey').val();
					console.log('inputPhone:'+inputPhone);//TODO
					if (seckill.validatePhone(inputPhone)) {
						//手机写入cookie
						$.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'});//有效期7天只在该路径下有效
						//刷新页面
						window.location.reload();
					}else {
						$('#killPhoneMessage').hide().html('<label class="label label-danger">手机号码错误！</label>').show(300);
					}
				});
				
			}
			//获取服务器时间显示倒计时
			var seckillId = params.seckillId;
			var startTime = params.startTime;
			var endTime = params.endTime;
			$.get(seckill.URL.now(),{},function(result){
				if (result && result.success) {
					var nowTime = result.data;
					seckill.countDown(seckillId, startTime, endTime, nowTime);
				}else {
					console.log(result);
				}
			});
		}
	}
}