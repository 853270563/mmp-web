<%@ page language="java" isErrorPage="true" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%><%  
response.setHeader("Cache-Control","no-store");//HTTP 1.1  
response.setHeader("Pragma","no-cache");//HTTP 1.0  
response.setDateHeader("Expires",0);//prevents caching at the proxy server  
%>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>大豐銀行系统</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="ctx" value="${pageContext.request.contextPath}/" />
<script language="javascript">
	var ctx = "${ctx}";
</script>
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="keywords" content="大丰銀行 网上銀行 手机銀行 电子銀行 銀行" />
<meta name="description" content="大丰銀行 网上銀行" />
<link rel="shortcut icon" href="favicon.ico" />
<link rel="stylesheet" href="${ctx}/css/theme/default/all.css">
<body>
	<div align="center" style="height: 300px;">
		<div class="div_tips" style="width: 600px; margin-top: 50px;">
			<div class="tips_header">
				<div class="tips_img"></div>
			</div>
			<div class="tips_content"
				style="background-color: #fff; height: 100px; margin: 5px;">
				<div>
					<div class="tips_title"></div>
					<span class="tips_text">网页已过期！ </span>
				</div>
			</div>
		</div>
		<div>
			<button data-role="submit" onclick="window.close();"
				class="ui-corner-all">请关闭页面</button>
		</div>
	</div>
</body>
</html>