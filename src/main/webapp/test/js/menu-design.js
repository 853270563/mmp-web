var menus = [
// 00基础服务
{name : "登录-WEB银行",path : "login_webLogin", clazz : "PP00" },
{name : "登录-个人移动",path : "login_mobileLogin", clazz : "PP00" }, 
{name : "登录-个人网银",path : "login_pbsLogin", clazz : "PP00" }, 
{name : "登录-企业网银",path : "login_pbsLogin", clazz : "PP00" }, 
{name : "前端菜单加载", path : "normal_menuListQuery", clazz : "PP00" }, 
{name : "用户名增加", path : "normal_userNameAdd",clazz : "PP00"}];

var tplMenus = [ '{@each menus as item}',
		'<button class="btn-menu PP ${item.clazz}"',
		' data-path="${item.path}" data-name="${item.name}"',
		' onclick="loadForm(this)">${item.name}</button>', '{@/each}' ]
		.join("");

function loadMenus(ftClass,ftCode,ftName) {
	try {
		var datas = [];
		if (ftClass || ftCode || ftName) {
			for (var i = 0, j = menus.length; i < j; i++) {
				var menu = menus[i];
				if (ftName && ftName.length > 0
						&& menu.name.toUpperCase().indexOf(ftName.toUpperCase()) >= 0) {
					datas.push(menu);
				} else if (ftCode && ftCode.length > 0
						&& menu.path.toUpperCase().indexOf(ftCode.toUpperCase()) >= 0) {
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
