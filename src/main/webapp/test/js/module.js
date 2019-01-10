var modules = [// 
{id : ""	,name : "全部"}, 
{id : "00"	,name : "基础"},
{id : "01"	,name : "安硕派单"},
{id : "02"	,name : "网贷"},
{id : "03"	,name : "影像平台"},
{id : "04"	,name : "贷后"},
{id : "05"	,name : "超时、审批，申请"},
{id : "06"	,name : "跑批任务"}


];

var tplModules = "{@each modules as item}"// 
		+ "<li data-value='PP${item.id}'>${item.id} ${item.name}</li> "// 
		+ "{@/each}";

function generyModules(elem) {
	var html = juicer(tplModules, {
		modules : modules
	});
	$(elem).html(html).find('li:first').after('<li><a href="'+getRootPath()+'/index.do" target=" target="_blank"">登录相关</a></li>');
}
function getRootPath(){
    var curWwwPath=window.document.location.href;
    var pathName=window.document.location.pathname;
    var pos=curWwwPath.indexOf(pathName);
    var localhostPaht=curWwwPath.substring(0,pos);
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    return(localhostPaht+projectName);
}