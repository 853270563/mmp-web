//过滤测试场景
function filterCases() {
	var filter = $("#filter").val().toUpperCase();
	$("#filter").val(filter);

	var datas = [];
	for (var i = 1, j = cases.length; i < j; i++) {
		var item = cases[i];
		if (item.code.toUpperCase().indexOf(filter) >= 0) {
			datas.push(item);
		}
	}
	return datas;
}
// 加载交易路由定义
function loadMenuInfo(url) {
	var datas = [];
	for (var i = 0, j = menus.length; i < j; i++) {
		var menu = menus[i];
		if (menu.url == url) {
			return menu;
		}
	}
	return null;
}

function initTestCases() {
	var datas = filterCases();
	var tpl = $("#CaseTpl").html();
	var html = juicer(tpl, {
		datas : datas
	});
	$("#trans-info").html(html);
}

// 导入测试交易
function loadTestDatas() {
	RunDatas = [];
	var caseItemTpl = $("#CaseItemTpl").html();
	$(".test-case").each(function() {
		var caseItem = $(this);
		var caseCode = caseItem.data("case");
		loadTestDatas4Case(caseItem, caseCode, caseItemTpl);
	});
}

// 加载单一场景的测试数据
function loadTestDatas4Case(jqObj, caseCode, caseItemTpl) {
	var url = "../test/loadTestData.do?caseCode=" + caseCode
	var ajax = new TransAjax();
	ajax.sendPostData(url, "", function(rpdata) {
		try {
			// 加工测试数据
			var LIST = rpdata.LIST;
			for (var i = 0, j = LIST.length; i < j; i++) {
				var data = LIST[i];
				var menu = loadMenuInfo(data.URL);
				data.name = menu ? menu.name : "";
				data.menu = menu;
				RunDatas.push(data);
			}
			var html = juicer(caseItemTpl, {
				datas : LIST
			});
			jqObj.find(".items").html(html);
		} catch (e2) {
			console.log(e2);
		}
	});
}

var RunDatas = [];
// 单线程运行
var runTestFlag = true;
function stopRunDatas() {
	runTestFlag = false;
}

function initRunDatas(obj) {
	runIndex = 0;
	errors = 0;
	// 设置可执行请求数
	setTotalRuns(RunDatas.length);
	runTestFlag = true;
	runTest();
}

var runIndex = 0;
var errors = 0;
function runTest() {
	if (!runTestFlag || runIndex >= RunDatas.length) {
		finished();
		return;
	}
	var data = RunDatas[runIndex];
	$("#" + data.FILE).find("i").html("");
	data.starttime = new Date().getTime();
	if (data.menu) {
		console.log("run test ", runIndex, " ", data);
	} else {
		console.log("run test ", runIndex, "error", data);
		runIndex++;
		// 设置已完成执行数
		setRuntimes(runIndex);
		runTest();
		return;
	}
	var transCode = data.menu.url;
	console.log("to send :" + data.CONTENT);
	testAjax(transCode, data.CONTENT, function(rpdata) {
		try {
			var data = RunDatas[runIndex];
			data.endtime = new Date().getTime();
			try {
				data.result = checkStatus(rpdata, data);
				data.response = JsonToStr(rpdata);
			} catch (e2) {
				data.result = false;
				data.response = "<red>系统异常，请自行查看通讯响应内容！</red>";
			}
			data.time = (data.endtime - data.starttime);
			var info = "[" + data.result + "] " + data.time + " ms";
			if (!data.result) {
				errors++;
				info = "<red>" + info + "</red><br>";
			} else {
				info = "<green>" + info + "</green><br>";
			}
			console.info("URL: " + data.FILE + " " + info);
			$("#" + data.FILE).find("i").html(info);
		} catch (e) {
			console.info(e);
		}
		runIndex++;
		// 设置已完成执行数
		setRuntimes(runIndex);
		runTest();
	});
}

function showTestDetail(filePath) {
	var data = findTestMenuItem(filePath);
	if (data == null)
		return;
	var menu = data.menu ? data.menu : {};
	$("#text_trans_name").val(menu.name);
	$("#text_trans_url").val(menu.url);
	$("#text_data_file").val(filePath);
	$("#text_trans_request").val(data.CONTENT);
	$("#text_trans_response").val(data.response ? data.response : "");
}

function findTestMenuItem(filePath) {
	for (var i = 0, j = RunDatas.length; i < j; i++) {
		var data = RunDatas[i];
		if (filePath == data.FILE) {
			return data;
		}
	}
	return null;
}

/**
 * 交易结果判断
 */
function checkStatus(rpdata, runData) {
	try {
		var transCode = runData.URL;
		var specTrans = runData.trans;
		if (specTrans) {
			for (var i = 0, j = specTrans.length; i < j; i++) {
				var specTran = specTrans[i];
				if (transCode == specTran.transCode) {
					// 反案例可指定某属性值进行比对
					var label = specTran.rtnCodeLabel ? specTran.rtnCodeLabel : "STATUS";
					if (rpdata[label] == specTran.rtnCode) {
						return true;
					}
					return false;
				}
			}
		}

	} catch (e) {
	}
	return "1" == rpdata.STATUS;
}
/*
 * 交易请求发起，可调整请求内容
 */
function testAjax(transCode, sendStr, callback) {
	var url = "../" + transCode;
	// var url="../root.do";
	if (url.indexOf(".do") == -1) {
		url += ".do";
	}
	sendStr = sendStr ? sendStr : "{}";
	var ajax = new TransAjax();
	ajax.sendPostData(url, sendStr, callback);
}

function clearStatus() {
	$(".trans-info .items i").html("--");
	errors = 0;
	setRuntimes(0);
	finished();
}

function finished() {
	runTestFlag = true;
	$("#btnRun").html("运行");
	console.log("--finished--");
}

// 总交易数
function setTotalTrans(cnt) {
	$(".totalTrans i").html(cnt);
}
// 可执行交易数
function setTotalRuns(cnt) {
	$(".totalRuns i").html(cnt);
}

// runtime
function setRuntimes(cnt) {
	$(".runtime i").html(cnt);
	$(".errors i").html(errors);
}

// 加载测试数据
function reload() {
	// 过滤测试场景
	initTestCases();
	// 刷新测试数据目录文件
	var url = "../test/refreshFileList.do";
	var ajax = new TransAjax();
	ajax.sendPostData(url, "", function(rpdata) {
		// 导入测试交易
		loadTestDatas();
	});
}

var hide = false;
function showOrHideMenus() {
	if (!hide) {
		$(".trans-info .items").hide();
	} else {
		$(".trans-info .items").show();
	}
	hide = !hide;
}
function showOrHideItems(obj) {
	var parent = $(obj).parent();
	parent.find(".items").toggle();
}

// 导出报告
function report() {
	var reportTpl = $("#ReportTpl").html();
	var html = juicer(reportTpl, {
		datas : RunDatas,
		total : RunDatas.length,
		oks : (RunDatas.length - errors),
		errors : errors
	});
	$("#REPORT_PANEL").html(html);
	print();
}

$(function() {
	var filterName = sessionStorage.getItem("filter");
	if (filterName) {
		$("#filter").val(filterName);
	}
	reload();
});