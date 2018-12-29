<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<html>
<head>
<meta name="keywords" content="移动营销 網上銀行 手機銀行 電子銀行 銀行" />
<meta name="description" content="移动营销 網上銀行" />
<title>移动营销</title>
<link rel="stylesheet" href="${ctx}css/theme/01/all.css">
<script src="${ctx}js/jquery-1.8.2.js"></script>
<script src="${ctx}js/jquery.easing.1.3.js"></script>
<script src="${ctx}js/tools/tabs/tabs.js"></script>
<script src="${ctx}js/ui/jquery.ui.core.js"></script>
<script src="${ctx}js/ui/jquery.ui.widget.js"></script>
<script src="${ctx}js/ui/jquery.ui.mouse.js"></script>
<script src="${ctx}js/ui/jquery.ui.sortable.js"></script>
<script src="${ctx}js/ui/jquery.ui.draggable.js"></script>
<script src="${ctx}js/plus.js"></script>
<script src="${ctx}js/util.js"></script>
<script src="${ctx}js/index.js"></script>
<style type="text/css">
body {
	background: #eee;
}
input{
	padding-left:4px;
	line-height: 25px;
}
H1{
	font-size:16px;
} 
.hiden{
	display:none;
}
#TRAN_AREA button{
	width: 250px;
	text-align:left;
	padding: 4px 4px;
	overflow-x: hidden;
	text-overflow :ellipsis ;
}
button.selected{
	background: #fff;
}
</style>
</head>
<body>
	<div id="TEST_PANEL" align="center">
		<div align="left" style="padding:10px 0px 0px 10px;height:50px;">
			<ul class="group trans_model" data-name="MODO_TYPE" data-group="MODO_TYPE" data-label="模块">
				<li data-value="PP" class="selected">全部服务</li>
				<li data-value="PP01">01 用户登录</li>
				<li data-value="PP02">02 应用管理</li>
				<li data-value="PP03">03 信息服务</li>
				<li data-value="PP04">04 文件上传</li>
				<li data-value="PP05">05 公共服务</li>
				<li data-value="PP06">06 对公开户</li>
				<li data-value="PP07">07 设备管理</li>
			</ul>
		</div> 
		<hr>
		<div id="TRAN_AREA" align="center" style="width:300px;max-width:300px;float:left;overflow:auto; padding:10px 0px;">
		</div>
		<div style="float:left;width:60%;">
                <div align="left">
                    <b>交易路径:</b><input id="TRAN_URL" type="text" size="40" value="" placeholder="请选择交易">&nbsp;&nbsp;
                    <button onclick="test()">测试</button>
                    <button onclick="query()" title="查找交易">查找</button>
                    <button onclick="clean()" title="清理响应">清理</button>
                    <input type="checkbox" id="showRspDefines" checked="true"/>显示响应报文
                    <img id="imgCode" src="common/ImageCode.do" onclick="refreshCode()">
                </div>

            <hr>
            <div align="left">
                <b>测试数据:</b>
                <input type="text" id="fileName" size="15" placeholder="输入保存的名称" ><button onclick="save()">保存</button>
                <select id="testData" onchange="showData(this)"></select>
            </div>
            <hr>
			<div align="left">
				<h1>交易请求</h1>
				<div id="REQ_FORM" class="bg01"></div>
				<h1>请求报文</h1>
				<div class="bg01">
					<textarea id="REQ_DATA" rows="5" cols="130">{"ORG_PAR_ID":""}</textarea>
				</div>
				<h1>响应报文</h1>
				<div class="bg01">
					<textarea id="RSP_DATA" rows="5" cols="130"></textarea>
				</div>
				<div id="RSP_FORM" class="bg01"></div>
			</div>
			<hr>  
		</div>
	</div>
</body>
<script src="${ctx}js/auto_plugin.js"></script>
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
		alert("session time out");
	}

	// 更新图片
	function refreshCode(){
		var imgCode=document.getElementById("imgCode");
		imgCode.src="common/ImageCode.do?a="+(new Date().getTime());
	}

	var showRspDefines=document.getElementById("showRspDefines");
	var lastBtn=null;
	function loadForm(obj) {
		if(lastBtn){
			$(lastBtn).removeClass("selected");
		}
		lastBtn = obj;
		var trans = $(obj).addClass("selected");
		$("#TRAN_URL").val(trans.attr("data-url"));
		var dataPath=trans.attr("data-path");
		var url = "${ctx}test/form/req.do?filePath=" + dataPath + "&v=" + new Date().getTime();
		$("#REQ_FORM").load(url);
		$("#RSP_DATA").val("");
		// 显示响应报文定义
		if(showRspDefines.checked){
			var url = "${ctx}test/form/rsp.do?filePath=" + dataPath + "&v=" + new Date().getTime();
			$("#RSP_FORM").load(url);
		}else{
			$("#RSP_FORM").html("");
		}
        loadTestData();
	}

    //加载测试shuj
    function loadTestData() {
        var transCode = $("#TRAN_URL").val();
        var url = "${ctx}test/qdzhFind.do?transCode=" + transCode;
        var ajax = new TransAjax();
        ajax.sendPostData(url, "{}", function(rpdata) {
            var list = rpdata.LIST;
            var sb = [];
            sb[sb.length] = "<option></option>";
            for (var i = 0; i < list.length; i++) {
                var map = list[i];
                sb[sb.length] = "<option value="+map.CONTENT+">" + map.NAME
                + "</option>";
            }
            $("#testData").html(sb.join());

        });

    }


    function test() {
		var url = $("#TRAN_URL").val();
		var data = $("#REQ_DATA").val();
		var ajax = new TransAjax();
		ajax.sendPostData(url, data, function(rpdata) {
//			alert("-------test callback-----\n" + JsonToStr(rpdata));
			var rst = JsonToStr(rpdata).replace(/\}/g, "}\n").replace(/\[/g,
					"[\n");
			$("#RSP_DATA").val(rst);

		});
	}

	$(function() {
		$("#TRAN_AREA").load("${ctx}test/trans.do");
		var panel = $("#TEST_PANEL");
		var group = panel.find("ul.trans_model>li");
        group.each(function(index) {
			$(this).bind(
					"click",
					function() {
						$(this).parent().find("li").removeClass("selected");
						$(this).addClass("selected");
						var model = $(this).attr("data-value");
						var btns = $("#TRAN_AREA>button").hide().filter(
								"." + model).show();

					});
		});

	});


    function save() {
        var data = $("#REQ_DATA").val();
        var transCode = $("#TRAN_URL").val();
        if (data == '' || transCode == '') {
            alert("没有需要保存的信息");
        }
        var url = "${ctx}test/qdzhSave.do?transCode=" + transCode + "&fileName="
                + $("#fileName").val();
        var ajax = new TransAjax();
        ajax.sendPostData(url, data, function(rpdata) {
            if (rpdata.STATUS == "1") {
                alert("保存成功！");
                loadTestData();
            } else {
                alert(rpdata.MSG || "保存失败！");
            }
        });
    }
    //显示历史数据
    function showData(o) {
        var value = o.value;
        $("#REQ_DATA").val(value);
        var json = eval("(" + value + ")")
        $(".formInput").each(function() {
            var name = this.name;
            $(this).val(json[name] || "");
        });
    }

    function query(){
        var s = $("#TRAN_URL").val();
        if(!s) {
            $("#TEST_PANEL").find("button").show();
        } else {
            $("#TEST_PANEL").find("button[data-url]").each(function() {
                var $thiz = $(this);
                var path = $thiz.attr("data-url");
                var text = $thiz.text();
                if(path && path.indexOf(s) != -1 || text && -1 != text.indexOf(s)) {
                    $thiz.show();
                } else {
                    $thiz.hide();
                }
            });
        }
    }

    function stringLength(str) {
        return str.replace(/[^\x00-\xff]/g, "**").length;
    }

    function clean() {
        $("#RSP_DATA").val("");
    }
</script>
</html>
