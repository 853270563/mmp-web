<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<html>
<head>
<title>大豐銀行</title>
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
</style> 
</head>
<body>
	<div id="form" class="form">
		<div><b>接口名称：${conf.name } <br>接口描述：${conf.desc } </b></div>
		<c:forEach items="${items}" var="item">
			<div>
				<label class="label">${item.desc} [${item.name}]:</label>
				<input name="${item.name}" data-type="text" class="formInput" data-name="${item.name}" 
					data-maxlength="${item.length }" data-minlength="" 
					value="${item.defaultValue }"
					data-required="${item['required']}"> 
				必需项[${item["required"]}]; 最大长度[${item.length }]; 字典[${item["mapKey"]}];  
			</div>
		</c:forEach>
		<br>
	</div>
</body>
<script type="text/javascript">
	$(function(){
		var form=$("#form");
		var reqData=$("#REQ_DATA");
		$(".formInput").bind("keyup",function(){ 
			initFormJson();
		});
		initFormJson();
		function initFormJson(){
			var str=JsonToStr(getFormJson(form,{}));
			reqData.val(str);
		}
	});
</script>
</html>
