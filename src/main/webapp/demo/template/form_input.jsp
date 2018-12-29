<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<html>
<head>
<%-- <%@ include file="/include/head.jsp"%> --%>
<meta name="description" content="模板：表单录入页面" />
<title>大丰銀行</title>
<style type="text/css">
</style>
</head>
<body>
	<!-- 导航栏 -->
	<div class="navbar">
		<ul>
			<li>当前位置：</li>
			<li>开发帮助</li>
			<li>表单录入页面</li>
		</ul>
	</div>
	<div class="page">	
		<div id="form_input" class="nopadding">
			<table class="ui-form" border="0" cellpadding="0" cellspacing="0">
				<tbody>
					<tr>
						<td width="30%" class="td-label">賬號：</td>
						<td width="180" class="td-input"><span id="fukuanzh">0020
								**** 6219</span></td>
						<td class="td-desc"></td>
					</tr>
					<tr>
						<td class="td-label">幣別：</td>
						<td class="td-input">
							<ul class="group" data-name="ccy" data-type="radio"
								data-group="chanel" data-label="幣別">
								<li data-value="MOP" class="selected">澳门币</li>
								<li data-value="HKD">港币</li>
							</ul>
						</td>
						<td class="td-desc"></td>
					</tr>
					<tr>
						<td class="td-label">收款賬號：</td>
						<td class="td-input"><input name="receiveAcctNo"
							data-label="收款賬號" required="required" data-type="money"
							placeholder="請輸入收款賬號" value="" type="text">
						</td>
						<td class="td-desc"></td>
					</tr>
					<tr>
						<td class="td-label">转账金额：</td>
						<td class="td-input"><input name="amount" data-label="转账金额"
							data-type="money" placeholder="請輸入转账金额" value="0.00" type="text">
						</td>
						<td class="td-desc"></td>
					</tr>
					<tr>
						<td class="td-label">最小值：</td>
						<td class="td-input"><input name="amount2" data-label="最小值"
							data-type="money" data-min="1000" placeholder="最小值" value="0.00"
							type="text"></td>
						<td class="td-desc"></td>
					</tr>
					<tr>
						<td class="td-label">最大值：</td>
						<td class="td-input"><input name="amount3" data-label="最大值"
							data-type="money" data-max="1000" placeholder="最大值" value="0.00"
							type="text"></td>
						<td class="td-desc">(备注：最小值为 1000 )</td>
					</tr>
					<tr>
						<td class="td-label">股数：</td>
						<td class="td-input"><input name="amount4" data-label="步进值"
							data-type="int" data-step="1000" placeholder="步进值" value="0"
							type="text"></td>
						<td class="td-desc">(备注：每手 1000 笔)</td>
					</tr>
					<tr>
						<td class="td-label">最大长度：</td>
						<td class="td-input"><input name="amount5"
							data-label="最大长度"
							data-type="text" maxlength="10" placeholder="最大长度" value=""
							type="text"></td>
						<td class="td-desc"></td>
					</tr>
					<tr>
						<td class="td-label">最小长度：</td>
						<td class="td-input"><input name="amount6" data-label="最小长度"
							data-type="text" data-minlength="6" placeholder="最小长度" value=""
							type="text"></td>
						<td class="td-desc"></td>
					</tr>
				</tbody>
			</table>
			<div style="padding-top:10px;">
				<button data-role="reset" class="ui-corner-all">重 置</button>
				<button data-role="submit" class="ui-corner-all">提 交</button>
				<button data-role="cancel" class="ui-corner-all">取 消</button>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript">
	$(function() {
		var panel = $("#form_input");
		// 单选
		var radioGroup = panel.find("ul[data-type$='radio']>li");
		radioGroup.bind("click", function() {
			$(this).parent().find("li").removeClass("selected");
			$(this).addClass("selected");
		});

		// 按钮
		buttons = panel.find("button");
		buttons.filter("[data-role$='submit']").bind("click", function() {
			var theme = $("#form_input");
			// 表单验证
			if (!validator($(theme))) {
				return false;
			}
			// 提交内容
			var sendObj = getFormJson(theme);
			alert(JsonToStr(sendObj));
			// 跳转页面,路径相对于demo/index.html
			//YT$.gotoPage('template/flow_next.html');
			var url = "${ctx}data/PD01001Op.do";
			var ajax = new TransAjax();
			ajax.sendPostData(url, JsonToStr(sendObj), function(rpdata) {
				var test=rpdata.forward_path;
				alert("-----success-------"+test);
			});

		});
		buttons.filter("[data-role$='reset']").bind("click", function() {
			// 错误提示清理
			validatorClean($("#form_input"));
		});
		// 初始化检验事件
		initValidate(panel);
	});
</script>
</html>