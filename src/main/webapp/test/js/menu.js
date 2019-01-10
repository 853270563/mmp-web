var menus = [{"clazz":"PP00","desc":"","name":"测试交易","path":"in/test/testsample","url":"test/testsample"}]; 

var tplMenus = [ '{@each menus as item}',
		'<button class="PP ${item.clazz}" data-url="${item.url}" ',
		' data-path="${item.path}" ',
		' onclick="loadForm(this)">${item.name}</button>', '{@/each}' ]
		.join("");

function loadMenus(ftClass, ftCode, ftName) {
	try {
		var datas = [];
		if (ftClass || ftCode || ftName) {
			for (var i = 0, j = menus.length; i < j; i++) {
				var menu = menus[i];
				if (ftName && ftName.length > 0
						&& menu.name.indexOf(ftName) >= 0) {
					datas.push(menu);
				} else if (ftCode && ftCode.length > 0
						&& menu.url.indexOf(ftCode) >= 0) {
					datas.push(menu);
				} else if (ftClass && ftClass.length > 0
						&& menu.clazz.indexOf(ftClass) >= 0) {
					datas.push(menu);
				}
			}
		} else {
			datas = menus;
		}
		var html = juicer(tplMenus, {
			menus : datas
		});
		$("#TRAN_AREA").html(html);
	} catch (e) {
		alert(e);
	}
}
