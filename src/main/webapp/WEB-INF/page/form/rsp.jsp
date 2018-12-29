<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<html>
<head>
<title>ares移动营销</title>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tlds/fn.tld" prefix="fn"%>
<%@ taglib uri="/WEB-INF/tlds/fmt.tld" prefix="fmt" %>
<script src="${webPath}js/jquery-1.8.2.js"></script>
<style type="text/css">
	.form *{
	     line-height: 25px;
	     text-valign: middle;
	}
	.label{ 
		min-width:220px;
		display:inline-block;
	}
	.form div{
		padding-left:20px;
	}
</style> 
</head>
<body>
	<div class="form">
		<c:forEach items="${items}" var="item">
			<div>
				<label class="label">${item.desc} [${item.name}]:</label>
				<c:if test="${not empty item.children}"> <b>列表结构</b> </c:if>
				最大长度[${item.length }]; 
				<c:if test="${not empty  item.mapKey}">
					字典[${item["mapKey"]}]; 字典字段[${item["descName"]}]; 
				</c:if>
				<c:forEach items="${item.children}" var="child"> 
					<div>
						<label class="label">${child.desc} [${child.name}]:</label>
						<c:if test="${not empty  child.mapKey}">
							字典[${child["mapKey"]}]; 字典字段[${child["descName"]}]; 
						</c:if>
					</div> 
				</c:forEach>
			</div>
		</c:forEach>
		<br>
	</div>
</body> 
</html>
