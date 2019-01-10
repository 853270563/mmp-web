<#assign entityLable = table.remarks?replace("表", "") />
<#assign basePath = "${r'${ctx}'}/${moduleName}/${table.className}/" />
<#assign basePerm = "${moduleName}:${table.className}" />
<#assign entityIdRef = "${r'${'}entry.${table.primaryField.name}}" />
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${entityLable}管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${basePath}">${entityLable}列表</a></li>
		<li class="active"><a href="${basePath}form?isView=${r'${param.isView}'}&id=${entityIdRef}">
		${entityLable}<tags:autoFormLabel editPermission="${basePerm}:edit" id="${entityIdRef}" /></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="entry" action="${basePath}save" method="post" class="form-search form-horizontal">
        <tags:message />
        <div class="container-fluid">
			<div class="row">
			<#list table.fieldList as field>
                <div class="control-group span6">
                    <label class="control-label">${field.remarks}：</label>
                    <div class="controls">
						<#if field.javaTypeName == 'Integer' || field.javaTypeName == 'Long' >
                            <form:input path="${field.name}" class="number"/>
						<#elseif field.javaTypeName == 'Date'>
                            <input id="${field.name}" name="${field.name}" type="text" readonly="readonly" maxlength="20"
                                   class="Wdate" value="<fmt:formatDate value="${r'${entry.'}${field.name}}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
						<#else>
                            <form:input path="${field.name}" />
						</#if>
                    </div>
                </div>
			</#list>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="${basePerm}:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="goBack('${basePath}')"/>
		</div>
	</form:form>
</body>
</html>