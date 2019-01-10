var menus = [

{name:"6手机号修改",url:"gmXmlService.do",path:"in/gm/gmMobileChange",clazz:"PP01"},
{name:"7登录密码重置",url:"gmXmlService.do",path:"in/gm/gmResetLoginPwd",clazz:"PP01"},
{name:"8登录解锁限制解除",url:"gmXmlService.do",path:"in/gm/gmReleaseLoginLmt",clazz:"PP01"},
{name:"9电子渠道锁定、解锁",url:"gmXmlService.do",path:"in/gm/gmModChannelStatus",clazz:"PP01"},
{name:"10账户加挂",url:"gmXmlService.do",path:"in/gm/gmHangAccount",clazz:"PP01"},
{name:"11客户信息查询",url:"gmXmlService.do",path:"in/gm/gmQueryCustBaseInfo",clazz:"PP01"},
{name:"12客户信息查询(查询客户账户加挂信息)",url:"gmXmlService.do",path:"in/gm/gmQueryHungAccountsInfo",clazz:"PP01"},
{name:"13电子渠道解约",url:"gmXmlService.do",path:"in/gm/gmTerminateChannel",clazz:"PP01"},
{name:"14电子客户开户",url:"gmXmlService.do",path:"in/gm/gmOpenAccount",clazz:"PP01"},
{name:"15客户认证方式管理",url:"gmXmlService.do",path:"in/gm/gmManagerAuthType",clazz:"PP01"},
{name:"16落地转账查询",url:"gmXmlService.do",path:"in/gm/gmLandTransferQuery",clazz:"PP01"},
{name:"17发送短信",url:"gmXmlService.do",path:"in/gm/gmSendMsg",clazz:"PP01"}
];

var tplMenus = [ '{@each menus as item}',
		'<button class="PP ${item.clazz}" data-url="${item.url}" ',
		' data-path="${item.path}" ',
		' onclick="loadForm(this)">${item.name}</button>', '{@/each}' ]
		.join("");

function loadMenus() {
	try {
		var html = juicer(tplMenus, {
			menus : menus
		});
		$("#TRAN_AREA").html(html);
	} catch (e) {
		alert(e);
	}
}
