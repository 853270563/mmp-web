var modules = [// 
{id : ""	,name : "全部"}, 
{id : "00"	,name : "基础服务"}

];

var tplModules = "{@each modules as item}"// 
		+ "<li data-value='PP${item.id}'>${item.id} ${item.name}</li> "// 
		+ "{@/each}";

function generyModules(elem) {
	var html = juicer(tplModules, {
		modules : modules
	});
	$(elem).html(html);
}