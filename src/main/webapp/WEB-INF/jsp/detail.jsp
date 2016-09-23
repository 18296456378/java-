<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="common/tag.jsp" %>
<!DOCTYPE html>
<html>
<head>
	<title>秒杀详情页</title>
	<%@ include file="common/head.jsp"%>
</head>
<body>
	<div class="container">
		<div class="panel panel-default">
			<div class="panel-heading text-center">
				<h1>${seckill.name}</h1>
			</div>
			<div class="panel-body  text-center">
				<h2 class="text-danger">
					<!-- 显示时间图标 -->
					<span class="glyphicon glyphicon-time"></span>
					<!-- 展示倒计时 -->
					<span class="glyphicon" id="seckill-box">
					</span>
				</h2>
			</div>
		</div>
	</div>
	<!-- 模态框（Modal） -->
	<div id="killPhoneModal" class="modal fade">
	    <div class="modal-dialog">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h3 class="modal-title" id="myModalLabel">
	                	<span class="glyphicon glyphicon-phone">
	                		秒杀电话：
	                	</span>
	                </h3>
	            </div>
	            <div class="modal-body">
	            	<div class="row">
	            		<div class="col-xs-8 col-xs-offset-2">
	            			<input type="text" name="killPhone" id="killPhoneKey"
	            			 placeholder="填手机号( ^_^ )" class="from-control"/>
	            		</div>
	            	</div>
	            </div>
	            <div class="modal-footer">
	            <!-- 验证信息 -->
	            	<span class="glyphicon" id="killPhoneMessage"></span>
	                <button type="button" id="killPhoneBtn" class="btn btn-success">
	                	<span class="glyphicon glyphicon-phone"></span>
	                	Submit
	               	</button>
	            </div>
	        </div><!-- /.modal-content -->
	   </div><!-- /.modal-dialog -->
	</div>
	<!-- /modal -->
</body>
	<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<!-- jQuery cookie插件操作 -->
	<script type="text/javascript" src="http://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
	<!-- jQuery countDown倒计时插件 -->
	<script type="text/javascript" src="http://cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.min.js"></script>
	<!-- 开始编写交互逻辑 -->
	<script type="text/javascript" src="${prc}/resources/script/seckill.js"></script>
	<script type="text/javascript">
		$(function(){
			//使用EL表达式传入参数
			seckill.detail.init({
				seckillId:"${seckill.seckillId}",
				startTime:"${seckill.startTime.time}",//毫秒,方便做解析
				endTime:"${seckill.endTime.time}"
			})
		})
	</script>
</html>