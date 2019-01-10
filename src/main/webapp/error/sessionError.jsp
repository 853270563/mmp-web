<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<html>
<head>
<%@ include file="/include/head.jsp"%>
<title> 大豐銀行系統-主菜單</title>
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="keywords" content="大丰銀行 网上銀行 手机銀行 电子銀行 銀行" />
<meta name="description" content="大丰銀行 网上銀行" />
<title>大丰銀行</title>
<link rel="shortcut icon" href="favicon.ico" />
<link rel="stylesheet" href="${ctx}css/theme/default/all.css" />
<script language="javascript">
	var ctx = "${ctx}";
	function logout(){
		top.location.href='${ctx}login.do?v='+new Date().getTime();
	}
</script>
</head>
<body>
	<div id="sessout" align="center" style="height:300px;">
		<div class="div_tips" style="max-width:600px;min-width:400px;margin-top:50px;">
			<div class="tips_header">
			  	<div class="tips_img"></div>
			</div>
			<div class="tips_content" style="background-color: #fff;height:100px;margin:5px;">
			    <div><div class="tips_title"></div><span class="tips_text">会话已过期 ，请重新登录 </span></div>
			</div>
		</div>
		<div>
			<button class="ui-corner-all" onclick="logout();">重新登录</button>
		</div>
	</div>
</body>
</html>
