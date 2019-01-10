/**
 * @description 公共交易请求 TransAjax
 * @return
 */
var TransAjax = function() {
	return this;
};
TransAjax.prototype = {
	timeout : 120000,// 超时时长
	timeoutflg : false,// 是否超时信息的标记
	_args : null, // 自定义参数
	_rpdata : null,
	_showError : false,
	_resetSessTimer : true,// 是否更新会话定时器
	getRpdata : function() {
		return this._rpdata;
	},
	setArgs : function(obj) {
		this._args = obj;
	},
	resetSessTimer : function(flag) {
		this._resetSessTimer = flag;
	},
	getArgs : function() {
		return this._args;
	},
	setShowError : function(s) {
		// 设置是否显示错误信息
		this._showError = s;
	},
	start : function() {
		// 开启超时提醒
		this.timeoutflg = true;
	},
	clear : function() {
		// 取消超时提醒时，需调用此方法。
		this.timeoutflg = false;
		if (this._timeoutHandle) {
			clearTimeout(this._timeoutHandle);
		}
	},
	getXmlHttpObj : function() {
		var xmlHttpObj;
		try {
			xmlHttpObj = new XMLHttpRequest();
		} catch (e) {
			try {
				xmlHttpObj = new ActiveXObject("Msxml2.XMLHTTP");
			} catch (e) {
				xmlHttpObj = new ActiveXObject("Microsoft.XMLHTTP");
			}
		}
		return xmlHttpObj;
	},
	sendPostData : function(url, param, callback) {
		var _ajax = this;
		_ajax.start();
		var xhr = this.getXmlHttpObj();
		xhr.onreadystatechange = function() {
			if (this.readyState == 4) {
				if (this.status == 200) {
					if (_ajax.abort) {
						alert("timeout is abort!");
						return;
					}
					var rpdata = eval("(" + this.responseText + ")");
					_ajax._rpdata = rpdata;
					var run = true;
					try {
						if (rpdata && rpdata.STATUS) {
							if (rpdata.STATUS == "005") {
								alert("session超时");// session超时;
								run = false;
							} else if (rpdata.STATUS == "006") {
								alert("当前用户已在其他设备上登录");
								run = false;
							} else if (rpdata.STATUS == "100") {
								run = false;
							} else if (rpdata.STATUS != "1") {
								if (_ajax._showError) {
									alert("" + rpdata.MSG);
								}
							}
						}
						if (run)
							callback(rpdata, _ajax._args);
					} catch (e) {
						//alert("TransAjax", e);
					}
					_ajax.clear();
				}
			}
		};
		xhr.open("POST", url, true);
		xhr.setRequestHeader('Content-Type', 'application/json');
		xhr.send(param);
		// Timeout checker
		if (_ajax.timeout > 0) {
			_ajax._timeoutHandle = setTimeout(function() {
				// Check to see if the request is still happening
				if (xhr && _ajax.timeoutflg) {
					_ajax.abort = true;
					xhr.abort();
					// alert("交易超时！");
				}
			}, _ajax.timeout);
		}
	}
};

/**
 * JSON对象转String
 * 
 * @param o
 * @returns {String}
 */
function JsonToStr(obj, prev, lvl) {
	if (obj == null) {
		return '""';
	}
	lvl = lvl || 0;
	var lvl2 = lvl + 1;
	var prev2 = prev ? (prev + " ") : null;
	prev = prev || "";
	switch (typeof (obj)) {
	default:
	case 'number':
	case 'string':
		return '"' + obj + '"';
	case 'object': {
		if (obj instanceof Array) {
			var strArr = [];
			var len = obj.length;
			for (var i = 0; i < len; i++) {
				strArr.push(JsonToStr(obj[i], prev2, lvl2));
			}
			return '[' + strArr.join(',') + ']';
		} else {
			var arr = [];
			for ( var i in obj) {
				var s = JsonToStr(obj[i], prev2, lvl2);
				arr.push(prev + '"' + i + '":' + s);
			}
			return (lvl > 0 ? prev : "") + "{" + arr.join(',') + "}";
		}
	}
	}
	return '""';
};

function getFormJson(panel, sendObj) {
	sendObj = sendObj ? sendObj : {};
	var grps = [];
	var grpKeys = {};
	var grpDatas = {};
	var lastGrp = "";
	// 表单异步提交
	panel.find("[data-type]").each(function() {
		var jqObj = $(this);
		var name = '', value = '';
		name = jqObj.attr("name");
		groupName = jqObj.attr("data-group");
		value = jqObj.val();
		if (name && groupName) {
			var index = $.inArray(groupName, grps);
			(index < 0) && (grps.push(groupName));

			var gkeys = grpKeys[groupName];
			!(gkeys) && (gkeys = grpKeys[groupName] = []);
			var index2 = $.inArray(name, gkeys);
			(index2 < 0) && (gkeys.push(name));

			var gDatas = grpDatas[groupName];
			!(gDatas) && (gDatas = grpDatas[groupName] = {});

			var gItemDatas = gDatas[name];
			!(gItemDatas) && (gItemDatas = gDatas[name] = []);
			gItemDatas.push(value ? value : "");

		}
		if (name && value) {
			if ($.browser.msie) {
				var placeholder = jqObj.attr("placeholder");
				if (placeholder == value) {
					value = "";
				}
			}
			if (groupName) {
			} else {
				sendObj[name] = value;
			}
		}
	});
	//console.log("grps:", grps);
	//console.log("grpKeys:", grpKeys);
	//console.log("grpDatas:", grpDatas);
	// 分离列表内容
	if (grps.length > 0) {
		//console.log("------grp---x 1-------");
		for (var i = 0, j = grps.length; i < j; i++) {
			var grpName = grps[i];
			var grpItems = grpKeys[grpName];
			//console.log("------grp---x 2-------");
			var firstKey = grpItems[0];
			var keySize = grpItems.length;
			//console.log("------grp---x 4-------");
			var grpItemDatas = grpDatas[grpName];
			var size = grpItemDatas[firstKey].length;
			//console.log("------grp---x 5-------");
			var datas = [];
			for (var i2 = 0; i2 < size; i2++) {
				var map = {};
				for (var i3 = 0; i3 < keySize; i3++) {
					var key = grpItems[i3];
					map[key] = grpItemDatas[key][i2];
				}
				datas.push(map);
			}
			sendObj[grpName] = datas;// 只支持一个 ListGroup
		}
	}
	return sendObj;
}