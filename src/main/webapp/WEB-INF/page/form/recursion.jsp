<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tlds/fn.tld" prefix="fn"%>
	
	
	
				
				<c:forEach items="${item.children}" var="child"> 
					<div>
						<label class="label">${child.desc} [${child.targetName}]:</label>
						<c:if test="${not empty child.children}"> <b>列表结构</b> </c:if>
							最大长度[${child.length }]; 
						<c:if test="${not empty  child.mapKey}">
							字典[${child["mapKey"]}]; 字典字段[${child["descName"]}]; 
						</c:if>
					   <c:set var="item" value="${child}" scope="request"/>  
                        <jsp:include page="/WEB-INF/page/form/recursion.jsp"/> 
					</div>
                    </c:forEach> 
	
