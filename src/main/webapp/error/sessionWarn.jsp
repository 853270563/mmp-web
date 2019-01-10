<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<html>
<head>
<%-- <%@ include file="/include/head.jsp"%> --%>
<title>湖北銀行系統-主菜單</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>湖北銀行</title> 
<script language="javascript">
	var ctx = "${ctx}";
</script>
<style type="text/css">
	.timeout_div{
		width: 380px;
		height: 260px;
	}
	.timeout_msg{
		height: 200px;
	}
	
	.msg_beginning{
		padding-left:20px;
		padding-bottom: 5px;
		line-height: 30px;
		height: 30px;
	}	
	.userName_span{
		color:#387599;
	}
	.msg_mainbody{
		line-height:30px;
		height: 90px;
		text-align:left;
		padding-bottom: 5px;
	}
	.msg_finish{
		text-align: center;
		padding-top: 10px;
		font-size: 20px;
		font-weight: bold;
	}
	.overtime{
		color: #800003;
		font-size: 50px;
	}
</style>
</head>
<body> 
	<div class="timeout_div" id="sessout">
		<div class="timeout_msg">
			<div class="msg_beginning">
				尊敬的<span class="userName_span">
					${CustName}
				</span>
				<c:if test="${CustSex eq 1}">先生</c:if><c:if test="${CustSex eq 0}">女士</c:if>
			</div>
			<div class="msg_mainbody">
				　　您的網上銀行已閒置了8分鐘，為了您網上銀行的信息安全，系統將在2分鐘倒計時后，自動退出。請確認是否繼續操作？
			</div>
			<div class="msg_finish">
				2分鐘倒計時剩餘時間：<span class="overtime">120</span>秒
			</div>
		</div>
		<div align="center" style="padding-top: 20px;">
			<button data-role="continue" class="ui-corner-all">繼續網上操作</button>
			&nbsp;&nbsp;
			<a href="${ctx}login.do"><button data-role="exit"  class="ui-corner-all">退出網上銀行</button></a>
		</div>
	</div>
</body> 
<script type="text/javascript">
	$(function() {
		var panel=$("#sessout");
		var buttons=panel.find("button");
		buttons.filter("[data-role$='continue']").bind("click",function(){
			// 显示串流页面 
			//alert("serverTypeFlag="+YT$.stockPageFlag+",STREAM_FLAG="+YT$.STREAM_FLAG);
			if (YT$.STREAM_FLAG == "0" && YT$.stockPageFlag == true){
				showStreamPageOT(); // 股票买卖串流页面时显示B区  
			}
			hideWindow("#window_panel_sess");
			YT$.sessionTimeout = false; 
			YT$.ressetSessionListener();
		});
		buttons.filter("[data-role$='exit']").bind("click",function(){
			location.href=ctx+"login.do";
		});
		YT$.wait=120; 
		YT$.timeDown=function(){
			if(!YT$.sessionTimeout){
				return;
			} 
			if (YT$.wait == 0) {
				location.href=ctx+"login.do";	
			} else { 
				$("#sessout").find("span.overtime").html(YT$.wait--);
				setTimeout("YT$.timeDown()",1000);
			}
		}
		YT$.timeDown();
	});
</script>
</html>
