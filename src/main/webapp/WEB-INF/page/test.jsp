<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<html>
<head>
<meta name="keywords" content="大豐銀行 網上銀行 手機銀行 電子銀行 銀行" />
<meta name="description" content="大豐銀行 網上銀行" />
<title>大豐銀行</title>
<link rel="stylesheet" href="${webPath}css/theme/01/all.css">
<script src="${webPath}js/jquery-1.8.2.js"></script>
<script src="${webPath}js/jquery.easing.1.3.js"></script>
<script src="${webPath}js/tools/tabs/tabs.js"></script>
<script src="${webPath}js/ui/jquery.ui.core.js"></script>
<script src="${webPath}js/ui/jquery.ui.widget.js"></script>
<script src="${webPath}js/ui/jquery.ui.mouse.js"></script>
<script src="${webPath}js/ui/jquery.ui.sortable.js"></script>
<script src="${webPath}js/ui/jquery.ui.draggable.js"></script>
<script src="${webPath}js/plus.js"></script>
<script src="${webPath}js/util.js"></script>
<script src="${webPath}js/index.js"></script>
<style type="text/css">
body {
	background: #eee;
}
H1{
	font-size:16px;
} 
.hiden{
	display:none;
}
</style>
</head>
<body>
	<div id="TEST_PANEL" align="center">
		<div align="left" style="padding-top:10px;height:25px;width:80%;">
			<ul class="group trans_model" data-name="MODO_TYPE" data-group="MODO_TYPE" data-label="模塊">
				<li data-value="PP" class="selected">全部服務</li>
				<li data-value="PP01">01 登錄相關</li>
				<li data-value="PP03">03 賬戶服務</li>
				<li data-value="PP04">04 轉賬服務</li>
			</ul>
		</div> 
		<div id="TRAN_AREA" align="left" style="width:80%; height:100px; overflow:auto; padding:10px 0px;">
			<button class="PP PP01" onclick="$('#TRAN_URL').val('00/CP001Op')">CP001Op：賬戶一覽</button>
			<button class="PP PP01" onclick="$('#TRAN_URL').val('00/CP002Op')">CP002Op：賬戶列表</button>
			

			<button class="PP PP01" onclick="$('#TRAN_URL').val('PD01001Op')">PD01001Op: 網銀登錄檢查</button>
			<button class="PP PP01" onclick="$('#TRAN_URL').val('PD01010Op')">PD01010Op: 修改密碼</button>
			<button class="PP PP01" onclick="$('#TRAN_URL').val('PD01012Op')">PD01012Op: 認證手機號</button> 
			<button class="PP PP01" onclick="$('#TRAN_URL').val('PD01030Op')">PD01030Op: 重置密碼</button>
			<button class="PP PP01" onclick="$('#TRAN_URL').val('PD01027Op')">PD01027Op: 認證手機號</button>
			<button class="PP PP01" onclick="$('#TRAN_URL').val('PD01028Op')">PD01028Op: 通過證件號查詢客戶信息</button>
			
			<button class="PP PP03" onclick="$('#TRAN_URL').val('PP03012Op')">PP03012Op: 刪除附屬賬戶（解約）</button>
			<button class="PP PP03" onclick="$('#TRAN_URL').val('PP03010Op')">PP03010Op: 查詢未添加賬戶</button>
			<button class="PP PP03" onclick="$('#TRAN_URL').val('PP03014Op')">PP03014Op: 添加附屬賬戶（簽約）</button>
			<button class="PP PP03" onclick="$('#TRAN_URL').val('PP03026Op')">PP03026Op: 交易記錄查詢</button>
			
			<button class="PP PP03" onclick="$('#TRAN_URL').val('PD03042Op')">PD03042Op: 申請支票簿</button>
			<button class="PP PP03" onclick="$('#TRAN_URL').val('PD03047Op')">PD03047Op: 報失支票簿</button>
			<button class="PP PP03" onclick="$('#TRAN_URL').val('PD03050Op')">PD03050Op: 存摺報失 </button> 
			
			<button class="PP PP04" onclick="$('#TRAN_URL').val('PD04012Op')">PD04012Op：同名戶轉賬（預覽）</button>
			<button class="PP PP04" onclick="$('#TRAN_URL').val('PD04022Op')">PD04022Op: 第三者轉賬（預覽）</button>
			<button class="PP PP04" onclick="$('#TRAN_URL').val('PD04032Op')">PD04032Op：澳中銀轉賬（預覽）</button>
			<button class="PP PP04" onclick="$('#TRAN_URL').val('PD04042Op')">PD04042Op：批量轉賬（預覽）</button> 
		</div> 
		<hr>
		<div align="left" style="width:80%;">
			交易路径:<input id="TRAN_URL" type="text" size="40" value="json/system/OrganAll.do">&nbsp;&nbsp;
			客戶號:<input id="CIF_NO" type="text" value="0000000009002941">&nbsp;&nbsp; 
			登錄號:<input id="IBS_LGN_ID" type="text" value="tfib0124">&nbsp;
			<button onclick="generySessionAcccounts()">登陸</button>
		</div>
		<hr>
		<div align="left" style="width:80%;">
			<h1>交易測試</h1> 
			<br>
			<span>請求報文</span>
			<div class="bg01">
				<textarea id="REQ_DATA" rows="5" cols="130">{"ORG_PAR_ID":""}</textarea>
			</div>
			<span>響應報文</span>
			<div class="bg01">
				<textarea id="RSP_DATA" rows="5" cols="130"></textarea>
			</div>
			<button onclick="test()">測試</button> 
			<ul class="group autotest" data-name="AUTO_FLAG" data-group="AUTO_FLAG" data-label="模塊">
				<li data-value="">自動測試</li>
			</ul>
		</div>
		<hr> 
		<div align="left" style="width:80%;">
			<span>成功交易</span>
			<div class="bg01" id="SUCC_TRANS_LIST"></div>
			<span>失敗交易</span>
			<div class="bg01" id="FAIL_TRANS_LIST"></div>
		</div>
	</div>
</body>
<script src="${webPath}js/auto_plugin.js"></script>
<script>
	/**
	* 請在此處設置會話創建時需要的客戶號及登錄號
	*/ 
	var ctx="${ctx}";
	YT$.loginPage=true;
	function generySessionAcccounts(){
		var cifNo = $("#CIF_NO").val();
		var tfbCifNo = "000000000"+$("#TFB_CIF_NO").val();
		var ibslgnId = $("#IBS_LGN_ID").val();
		var url = "${ctx}indexTest.do?cifNo=" + cifNo + "&ibsLgnId=" + ibslgnId+"&tfbCifNo="+tfbCifNo;
		var ajax = new TransAjax();
		ajax.sendPostData(url, JsonToStr({}), function(rpdata) {
		});
	}

	function showTimeOut() {
		//generySessionAcccounts();
	}

	function test() {
		var url = $("#TRAN_URL").val();
		var data = $("#REQ_DATA").val();
		var ajax = new TransAjax();
		ajax.sendPostData(url, data, function(rpdata) {
			alert("-------test callback-----\n" + JsonToStr(rpdata));
			$("#RSP_DATA").val(JsonToStr(rpdata));

		});
	} 
	
	$(function() {
		var panel = $("#TEST_PANEL");
		var group = panel.find("ul.trans_model>li");
		group.each(function(index){
			$(this).bind("click",function(){ 
				$(this).parent().find("li").removeClass("selected");
				$(this).toggleClass("selected");
				var model=$(this).attr("data-value"); 
				var btns=$("#TRAN_AREA>button").hide().filter("." + model).show();
				
			});
		});
		createTestDatas();
	});   
	
	
	function testBtt(data,funcCode){
		 $("#REQ_DATA").html(JsonToStr(data));
		 $("#TRAN_URL").val(funcCode);
	}
	
	//查詢賬戶詳情
	function queryAccountDetail(funcCode){
		var accountTypes = ['SA','CA','FS','FD','LN','MI'];
		var accountDetail = {
				"ACCT_TYP":"SA"
		};
		
		for(var i=0;i<accountTypes.length;i++){
			accountDetail.ACCT_TYP = accountTypes[i];
			 $("#REQ_DATA").html(JsonToStr(accountDetail));
			 YT$.debug = false;
			 var url = "data/" + funcCode + ".do";
			 var ajax = new TransAjax();
			 ajax.sendPostData(url, JsonToStr(accountDetail), function(rpdata) {
				 $("#RSP_DATA").val(JsonToStr(rpdata));

			 });
		}
	}
</script>
</html>
