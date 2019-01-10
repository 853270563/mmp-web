<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<html>
<head>
<title>移动营销</title>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c"%>
<script src="${ctx}js/jquery-1.8.2.js"></script>
</head>
<body>
	<!-- pp01 用户登录相关 -->
	<button class="ARES PP01" data-url="ares/login/ClientNoLogin.do"
			data-path="db/ares/login/ClientNoLogin" onclick="loadForm(this)">无会话登录交易</button>
	<button class="ARES PP01" data-url="ares/login/ClientLogin.do"
		data-path="db/ares/login/ClientLogin" onclick="loadForm(this)">登录交易</button>
	<button class="ARES PP01" data-url="ares/login/H5Login.do"
		data-path="db/ares/login/H5Login" onclick="loadForm(this)">H5登录</button>
	<button class="ARES PP01" data-url="common/Base64ImageCode.do"
		data-path="db/common/Base64ImageCode" onclick="loadForm(this)">base64验证码</button>
	<button class="ARES PP01" data-url="ares/login/ClientLogout.do"
		data-path="db/ares/login/ClientLogout" onclick="loadForm(this)">安全退出</button>
	<button class="ARES PP01" data-url="ares/login/loginExpandData.do"
		data-path="db/ares/login/loginExpandData" onclick="loadForm(this)">登录拓展数据管理</button>
	<button class="ARES PP01" data-url="ares/login/ChangePwd.do"
		data-path="db/ares/login/ChangePwd" onclick="loadForm(this)">修改密码</button>
		
	<button class="ARES PP01" data-url="ares/gstPwd/queryGstInfo.do"
		data-path="db/ares/gstPwd/queryGstInfo" onclick="loadForm(this)">查询手势密码</button>
	<button class="ARES PP01" data-url="ares/gstPwd/updateGstInfo.do"
		data-path="db/ares/gstPwd/updateGstInfo" onclick="loadForm(this)">设置手势密码</button>
	<button class="ARES PP01" data-url="ares/gstPwd/checkGstInfo.do"
		data-path="db/ares/gstPwd/checkGstInfo" onclick="loadForm(this)">校验手势密码</button>
	<button class="ARES PP01" data-url="ares/getToken.do"
		data-path="db/ares/getToken" onclick="loadForm(this)">获取token</button>
		
		
	<button class="ARES PP01" data-url="ares/login/BusiLogAdd.do"
			data-path="db/ares/login/BusiLog" onclick="loadForm(this)">日志提交</button>
	<button class="ARES PP01" data-url="ares/login/NetworkHeartbeat.do"
			data-path="db/ares/login/NetworkHeartbeat" onclick="loadForm(this)">网络心跳</button>
	<button class="ARES PP01" data-url="market/crud/list/ItemList.do"
		data-path="db/market/core/ItemList" onclick="loadForm(this)">二维数据字典(缓存)</button>
	
	<!-- pp02 设备管理相关 -->
	<button class="ARES PP02" data-url="ares/deviceInfo/eraseDeviceCheck.do"
			data-path="db/ares/deviceInfo/eraseDeviceCheck" onclick="loadForm(this)">擦除设备验证</button>
	<button class="ARES PP02" data-url="ares/deviceInfo/eraseDeviceResultRe.do"
			data-path="db/ares/deviceInfo/eraseDeviceResultRe" onclick="loadForm(this)">擦除结果反馈</button>
	<button class="ARES PP02" data-url="ares/deviceInfo/undoBoundDevice.do"
			data-path="db/ares/deviceInfo/undoBoundDevice" onclick="loadForm(this)">设备解绑</button>
	<button class="ARES PP02" data-url="ares/deviceInfo/boundDevice.do"
			data-path="db/ares/deviceInfo/boundDevice" onclick="loadForm(this)">设备注册</button>
	<button class="ARES PP02" data-url="ares/deviceInfo/queryDeviceApps.do"
			data-path="db/ares/deviceInfo/queryDeviceApps" onclick="loadForm(this)">黑白名单查询</button>
	<!-- MDM设备控制策略 -->
	<button class="ARES PP02" data-url="ares/deviceInfo/queryPolicyItemAppStart.do"
			data-path="db/ares/deviceInfo/queryPolicyItemAppStart" onclick="loadForm(this)">应用启动策略查询</button>
	<button class="ARES PP02" data-url="ares/deviceInfo/queryPolicyItemLogin.do"
			data-path="db/ares/deviceInfo/queryPolicyItemLogin" onclick="loadForm(this)">用户登陆策略查询</button>
	<button class="ARES PP02" data-url="ares/deviceInfo/violatePolicyLog.do"
			data-path="db/ares/deviceInfo/violatePolicyLog" onclick="loadForm(this)">违规日志上传</button>

	<!-- pp03 消息推送 -->
	<!-- <button class="ARES PP03" data-url="ares/mp/mpCustClientSub.do"
			data-path="db/ares/mp/mpCustClientSub" onclick="loadForm(this)">订阅消息</button>
	<button class="ARES PP03" data-url="ares/mp/mpCustClientUpdate.do"
			data-path="db/ares/mp/mpCustClientUpdate" onclick="loadForm(this)">取消订阅</button>
	<button class="ARES PP03" data-url="ares/mp/aresMpMsgTaskInsert.do"
			data-path="db/ares/mp/AresMpMsgTask" onclick="loadForm(this)">消息推送</button>
	<button class="ARES PP03" data-url="ares/mp/aresMpMsgTmplInsert.do"
			data-path="db/ares/mp/AresMpMsgTmpl" onclick="loadForm(this)">消息模板推送</button>
	<button class="ARES PP03" data-url="ares/mp/aresMpMsgTypeList.do"
			data-path="db/ares/mp/AresMpMsgType" onclick="loadForm(this)">消息类型列表</button>
	<button class="ARES PP03" data-url="ares/mp/custMsgList.do"
			data-path="db/ares/mp/custMsgList" onclick="loadForm(this)">客户消息列表</button> -->

	<!-- PP04 应用管理接口 -->
	<button class="ARES PP04" data-url="ares/appManage/getApkUpdateInfo.do"
			data-path="db/ares/appManage/getApkUpdateInfo" onclick="loadForm(this)">原生资源更新</button>
	<button class="ARES PP04" data-url="ares/appManage/getHtmlUpdateInfo.do"
			data-path="db/ares/appManage/getHtmlUpdateInfo" onclick="loadForm(this)">HTML资源更新</button>
	<button class="ARES PP04" data-url="ares/appManage/fileDownLoad.do"
			data-path="db/ares/appManage/fileDownLoad" onclick="loadForm(this)">资源包下载</button>
	<button class="ARES PP04" data-url="ares/appManage/oneFileDownLoad.do"
			data-path="db/ares/appManage/fileDownLoad" onclick="loadForm(this)">单文件下载</button>
	<button class="ARES PP04" data-url="ares/appManage/roleApp.do"
			data-path="db/ares/appManage/roleApp" onclick="loadForm(this)">用户可下载应用</button>
	<button class="ARES PP04" data-url="ares/appManage/saveAppVers.do"
			data-path="db/ares/appManage/saveAppVers" onclick="loadForm(this)">资源包上传（第三方）</button>
	
	<!-- PP05 资讯公告 -->
	<!-- <button class="PORTAL PP05" data-url="portal/note/loadNote.do"
			data-path="db/portal/note/loadNote" onclick="loadForm(this)">资讯公告加载</button>
	<button class="PORTAL PP05" data-url="portal/note/noteList.do"
			data-path="db/portal/note/noteList" onclick="loadForm(this)">资讯公告列表</button>
	PP06 通讯录
	<button class="PORTAL PP06" data-url="portal/txl/allTxlList.do"
			data-path="db/portal/txl/allTxlList" onclick="loadForm(this)">通讯录同步</button>
	<button class="PORTAL PP06" data-url="portal/txl/groupUpdate.do"
			data-path="db/portal/txl/groupUpdate" onclick="loadForm(this)">个人群组修改同步</button>
	PP07 Portal聚合信息
	<button class="PORTAL PP07" data-url="portal/infoGT/infoGTlist.do"
			data-path="db/portal/infoGT/infoGTlist" onclick="loadForm(this)">聚合信息列表</button>
	<button class="PORTAL PP07" data-url="portal/infoGT/loadInfoGT.do"
			data-path="db/portal/infoGT/loadInfoGT" onclick="loadForm(this)">聚合信息详情</button>
	<button class="PORTAL PP07" data-url="portal/infoGT/addInfoGT.do"
			data-path="db/portal/infoGT/addInfoGT" onclick="loadForm(this)">聚合信息添加</button>
	PP08 统一认证
	<button class="PORTAL PP08" data-url="portal/thirdApp/gainAuthCode.do"
			data-path="db/portal/thirdApp/gainAuthCode" onclick="loadForm(this)">获取授权码</button>
	<button class="PORTAL PP08" data-url="portal/thirdApp/gainAccessToken.do"
			data-path="db/portal/thirdApp/gainAccessToken" onclick="loadForm(this)">获取访问令牌</button>
	<button class="PORTAL PP08" data-url="portal/thirdApp/freshAccessToken.do"
			data-path="db/portal/thirdApp/freshAccessToken" onclick="loadForm(this)">刷新访问令牌</button>
	<button class="PORTAL PP08" data-url="portal/thirdApp/checkAccessToken.do"
			data-path="db/portal/thirdApp/checkAccessToken" onclick="loadForm(this)">验证访问令牌</button>


	
	PP10 业务包管理
	<button class="YDYX PP10" data-url="mm/myw/pageAdd/busiPageAdd.do"
			data-path="db/market/mm/myw/busiPage/busiPageAdd" onclick="loadForm(this)">添加包接口</button>
	<button class="YDYX PP10" data-url="mm/myw/pageList/busiPageList.do"
			data-path="db/market/mm/myw/busiPage/busiPageList" onclick="loadForm(this)">根据用户编号查询包列表以及业务列表</button>
	<button class="YDYX PP10" data-url="mm/myw/busiSelAdd/busiSelAdd.do"
			data-path="db/market/mm/myw/busiPage/busiSelAdd" onclick="loadForm(this)">添加业务功能[客户端暂未启用]</button>
	<button class="YDYX PP10" data-url="mm/myw/busiSelList/busiSelList.do"
			data-path="db/market/mm/myw/busiPage/busiSelList" onclick="loadForm(this)">业务功能查询[客户端暂未启用]</button>
	PP11 产品货架 [理财、基金]
	<button class="YDYX PP11" data-url="mm/mhj/prodInfo/next/ProdInfoList.do"
			data-path="db/market/mm/mhj/prodInfo/ProdInfoList" onclick="loadForm(this)">理财信息</button>	
	<button class="YDYX PP11" data-url="mm/mhj/value/list/IfsFundValue.do"
			data-path="db/market/mm/mhj/value/IfsFundValue" onclick="loadForm(this)">基金涨跌幅</button>
	<button class="YDYX PP11" data-url="mm/mhj/prodInfo/list/PeProdInfo.do"
			data-path="db/market/mm/mhj/prodInfo/PeProdInfo" onclick="loadForm(this)">基金产品详细</button>
			
	<button class="YDYX PP11" data-url="market/core/list/ProdCompany.do"
			data-path="db/market/core/ProdCompany" onclick="loadForm(this)">基金公司列表</button>

	PP12 工具管理[网点、资费、金融助手、利率]
	<button class="YDYX PP12" data-url="market/core/list/BankList.do"
			data-path="db/market/core/BankList" onclick="loadForm(this)">附近网点查询</button>
	<button class="YDYX PP12" data-url="market/core/list/AreaBankList.do"
			data-path="db/market/core/AreaBankList" onclick="loadForm(this)">区域网点查询</button>
	<button class="YDYX PP12" data-url="mm/mgj/core/AreaList.do"
			data-path="db/market/core/AreaList" onclick="loadForm(this)">区域静态数据同步</button>
			
	<button class="YDYX PP12" data-url="market/core/list/RateView.do"
			data-path="db/market/core/RateView" onclick="loadForm(this)">利率</button>
	<button class="YDYX PP12" data-url="market/core/list/ChargeList.do"
			data-path="db/market/core/ChargeList" onclick="loadForm(this)">资费标准</button>
	<button class="YDYX PP12" data-url="market/core/list/FinaServCont.do"
			data-path="db/market/core/FinaServCont" onclick="loadForm(this)">金融服务</button>
			
	<button class="YDYX PP12"	data-url="mm/mgr/calendar/next/ScheduleInfoQuery.do"
			data-path="db/market/mm/mgr/calendar/ScheduleInfoQuery" onclick="loadForm(this)">金融日历个人日程查询【分页】</button>
	<button class="YDYX PP12"	data-url="mm/mgr/calendar/query/ScheduleInfoQueryCount.do"
			data-path="db/market/mm/mgr/calendar/ScheduleInfoQueryCount" onclick="loadForm(this)">金融日历个人日常记录数查询</button>
	<button class="YDYX PP12" data-url="mm/mgr/calendar/add/ScheduleInfoAdd.do"
			data-path="db/market/mm/mgr/calendar/ScheduleInfoAdd" onclick="loadForm(this)">金融日历个人日程添加</button>
	<button class="YDYX PP12"	data-url="mm/mgr/calendar/update/ScheduleInfoUpdate.do"
			data-path="db/market/mm/mgr/calendar/ScheduleInfoUpdate" onclick="loadForm(this)">金融日历个人日程修改</button>
	<button class="YDYX PP12"	data-url="mm/mgr/calendar/delete/ScheduleInfoDelete.do"
			data-path="db/market/mm/mgr/calendar/ScheduleInfoDelete" onclick="loadForm(this)">金融日历个人日程删除</button>
	<button class="YDYX PP12" data-url="mm/mgr/calendar/next/TodoInfo.do"
			data-path="db/market/mm/mgr/calendar/TodoInfo" onclick="loadForm(this)">金融日历客户意向查询【分页】</button>
	<button class="YDYX PP12"	data-url="mm/mgr/calendar/query/TodoInfoQueryCount.do"
			data-path="db/market/mm/mgr/calendar/TodoInfoQueryCount" onclick="loadForm(this)">金融日历客户意向记录数查询</button>
	<button class="YDYX PP12" data-url="mm/mgr/calendar/add/TodoInfoAdd.do"
			data-path="db/market/mm/mgr/calendar/TodoInfoAdd" onclick="loadForm(this)">金融日历客户意向添加</button>
	<button class="YDYX PP12" data-url="mm/mgr/calendar/update/TodoInfoUpdate.do"
			data-path="db/market/mm/mgr/calendar/TodoInfoUpdate" onclick="loadForm(this)">金融日历客户意向修改</button>
	<button class="YDYX PP12" data-url="mm/mgr/calendar/delete/TodoInfoDelete.do"
			data-path="db/market/mm/mgr/calendar/TodoInfoDelete" onclick="loadForm(this)">金融日历客户意向删除</button>

	PP13 信息服务
	<button class="YDYX PP13" data-url="mm/mzj/propa/list/resFilesList.do"
			data-path="db/market/mm/mzj/resFilesList" onclick="loadForm(this)">广告及展架通用接口</button>
	<button class="YDYX PP13" data-url="mm/mzj/propaData/list/propaDataList.do"
			data-path="db/market/mm/mzj/propaDataList" onclick="loadForm(this)">产品展架接口</button>
	<button class="YDYX PP13" data-url="mm/mzj/propa/list/ResFileByUserList.do"
			data-path="db/market/mm/mzj/ResFileByUserList" onclick="loadForm(this)">广告及展架用户接口【未使用】</button>
			
			
	<button class="YDYX PP13" data-url="mm/sys/menu/menuAType/list/MenuAtypeList.do"
			data-path="db/market/mm/sys/menu/MenuAtypeList" onclick="loadForm(this)">客户端A面菜单-角色ID【停用】</button>
	<button class="YDYX PP13" data-url="mm/sys/menu/list/MenuList.do"
			data-path="db/market/mm/sys/menu/MenuList" onclick="loadForm(this)">客户端B面菜单-角色ID【停用】</button>
	<button class="YDYX PP13" data-url="mm/sys/menuAType/list/MenuUserAtypeList.do"
			data-path="db/market/mm/sys/menu/MenuUserAtypeList" onclick="loadForm(this)">客户端A面菜单-用户ID</button>
	<button class="YDYX PP13" data-url="mm/sys/menu/list/MenuUserBtypeList.do"
			data-path="db/market/mm/sys/menu/MenuUserBtypeList" onclick="loadForm(this)">客户端B面菜单-用户ID</button>
	<button class="YDYX PP13" data-url="market/core/list/NoteList.do"
			data-path="db/market/mm/mgr/note/NoteList" onclick="loadForm(this)">通知公告(停用)</button>
	<button class="YDYX PP13" data-url="mm/mgr/note/next/NoteList.do"
			data-path="db/market/mm/mgr/note/NoteListNext" onclick="loadForm(this)">通知公告【分页】(停用)</button>
	<button class="YDYX PP13" data-url="mm/mgr/prodshelf/add/FavoInfoAdd.do"
			data-path="db/market/mm/mgr/prodshelf/FavoInfoAdd" onclick="loadForm(this)">添加收藏</button>
	<button class="YDYX PP13" data-url="mm/mgr/prodshelf/delete/FavoInfoDelete.do"
			data-path="db/market/mm/mgr/prodshelf/FavoInfoDelete" onclick="loadForm(this)">删除收藏</button>
			
	<button class="YDYX PP13" data-url="market/core/list/ChargeProductList.do"
			data-path="db/market/core/ChargeProductList" onclick="loadForm(this)">资费产品</button>
	<button class="YDYX PP13" data-url="mm/mgg/message/SendCardMessage.do"
			data-path="db/market/mm/mgg/message/sendCardMessage" onclick="loadForm(this)">发送个人名片短信</button> -->

	<!-- PP14 移动信贷 -->
	<button class="YDYX PP14" data-url="creditTaskList/creditTaskList.do"
			data-path="db/creditInvestigate/waittingDone" onclick="loadForm(this)">待办任务查询</button>
			
	<button class="YDYX PP14" data-url="alreadyDone/alreadyDone.do"
			data-path="db/creditInvestigate/alreadyDone" onclick="loadForm(this)">已办任务查询</button>	
		<!-- begin -->
			<button class="YDYX PP14" data-url="personalCustInfo/personalCustInfo.do"
			data-path="db/creditInvestigate/personalCustInfo" onclick="loadForm(this)">个人客户信息</button>	
			<button class="YDYX PP14" data-url="groupCustInfo/groupCustInfo.do"
			data-path="db/creditInvestigate/groupCustInfo" onclick="loadForm(this)">集团客户概况 </button>	
			<button class="YDYX PP14" data-url="guaranteeInfo/guaranteeInfo.do"
			data-path="db/creditInvestigate/guaranteeInfo" onclick="loadForm(this)">抵押物信息详情 </button>	
			<button class="YDYX PP14" data-url="amountAssuranceDetail/amountAssuranceDetail.do"
			data-path="db/creditInvestigate/amountAssuranceDetail" onclick="loadForm(this)">授信额度担保详情信息</button>
		<!--  end-->
	<button class="YDYX PP14" data-url="showSign/showSign.do"
			data-path="db/creditInvestigate/isShowSign" onclick="loadForm(this)">是否显示签署意见</button>	
	<button class="YDYX PP14" data-url="showJudgeSign/showJudgeSign.do"
			data-path="db/creditInvestigate/showJudgeSign" onclick="loadForm(this)">是否显示贷审会意见</button>							
	<button class="YDYX PP14" data-url="custInfo/custInfo.do"
			data-path="db/creditInvestigate/custInfo" onclick="loadForm(this)">公司、小企业客户概况信息</button>
	<button class="YDYX PP14" data-url="custLegalRep/custLegalRep.do"
			data-path="db/creditInvestigate/custLegalRep" onclick="loadForm(this)">客户法人信息查询</button>		
	<button class="YDYX PP14" data-url="custStockHolder/custStockHolder.do"
			data-path="db/creditInvestigate/custStockHolder" onclick="loadForm(this)">客户股东信息查询</button>		
	<button class="YDYX PP14" data-url="custExecutives/custExecutives.do"
			data-path="db/creditInvestigate/custExecutives" onclick="loadForm(this)">客户高管信息查询</button>		
	<button class="YDYX PP14" data-url="custFinSta/custFinSta.do"
			data-path="db/creditInvestigate/custFinSta" onclick="loadForm(this)">客户财务报表信息查询</button>		
	<button class="YDYX PP14" data-url="baseInfo/baseInfo.do"		
			data-path="db/creditInvestigate/baseInfo" onclick="loadForm(this)">基本信息查询</button>		
	<button class="YDYX PP14" data-url="amountAllocate/amountAllocate.do"
			data-path="db/creditInvestigate/amountAllocate" onclick="loadForm(this)">授信额度分配查询</button>
	<button class="YDYX PP14" data-url="amountAssurance/amountAssurance.do"
			data-path="db/creditInvestigate/amountAssurance" onclick="loadForm(this)">授信额度担保查询</button>
	<button class="YDYX PP14" data-url="bussDocument/bussDocument.do"
			data-path="db/creditInvestigate/bussDocument" onclick="loadForm(this)">业务文档信息查询</button>							
	<button class="YDYX PP14" data-url="allOpition/allOpition.do"
			data-path="db/creditInvestigate/allOpition" onclick="loadForm(this)">所有意见信息查询</button>	
	<button class="YDYX PP14" data-url="currentOpitions/currentOpitions.do"
			data-path="db/creditInvestigate/currentOpitions" onclick="loadForm(this)">审批人意见选择</button>
	<button class="YDYX PP14" data-url="nextInfo/nextInfo.do"
			data-path="db/creditInvestigate/nextInfo" onclick="loadForm(this)">下一步审批信息</button>
	<button class="YDYX PP14" data-url="yxPicInfo/yxPicInfo.do"
			data-path="db/creditInvestigate/yxPicInfo" onclick="loadForm(this)">影像信息</button>
	<button class="YDYX PP14" data-url="invReport/invReport.do"
			data-path="db/creditInvestigate/invReport" onclick="loadForm(this)">调查报告</button>	
	<button class="YDYX PP14" data-url="keepAdivice/keepAdivice.do"
			data-path="ytbank/creditInvestigate/keepAdivice" onclick="loadForm(this)">保存意见</button>
	<button class="YDYX PP14" data-url="submitAction/submitAction.do"
			data-path="db/creditInvestigate/submitAction" onclick="loadForm(this)">提交</button>
	<button class="YDYX PP14" data-url="keepPjAdvice/keepPjAdvice.do"
			data-path="ytbank/creditInvestigate/keepPjAdvice" onclick="loadForm(this)">保存评级意见</button>
	<button class="YDYX PP14" data-url="assureTrue/assureTrue.do"
			data-path="ytbank/creditInvestigate/assureTrue" onclick="loadForm(this)">评级终审确认</button>
	<button class="YDYX PP14" data-url="clickGet/clickGet.do"
			data-path="ytbank/creditInvestigate/clickGet" onclick="loadForm(this)">点击签署意见按钮</button>
	<button class="YDYX PP14" data-url="wifeAndFamliy/wifeAndFamliy.do"
			data-path="ytbank/creditInvestigate/wifeAndFamliy" onclick="loadForm(this)">配偶或家庭主要成员情况</button>
	<button class="YDYX PP14" data-url="peopleEducation/peopleEducation.do"
			data-path="ytbank/creditInvestigate/peopleEducation" onclick="loadForm(this)">个人学业履历</button>
	<button class="YDYX PP14" data-url="peopleWorkList/peopleWorkList.do"
			data-path="ytbank/creditInvestigate/peopleWorkList" onclick="loadForm(this)">个人工作履历</button>
	<button class="YDYX PP14" data-url="isPersionOrGroup/isPersionOrGroup.do"
			data-path="ytbank/creditInvestigate/isPersionOrGroup" onclick="loadForm(this)">返回客户类型</button>
	<button class="YDYX PP14" data-url="groupCreditDisInfo/groupCreditDisInfo.do"
			data-path="ytbank/creditInvestigate/groupCreditDisInfo" onclick="loadForm(this)">集团授信额度</button>
	<button class="YDYX PP14" data-url="relateLimitInfo/relateLimitInfo.do"
			data-path="ytbank/creditInvestigate/relateLimitInfo" onclick="loadForm(this)">关联额度信息</button>
	<button class="YDYX PP14" data-url="estateMortgageType/estateMortgageType.do"
			data-path="ytbank/creditInvestigate/estateMortgageType" onclick="loadForm(this)">房产按揭类型</button>
	<button class="YDYX PP14" data-url="projectLicenseInfo/projectLicenseInfo.do"
			data-path="ytbank/creditInvestigate/projectLicenseInfo" onclick="loadForm(this)">项目许可证信息</button>
	<button class="YDYX PP14" data-url="carMortgagetype/carMortgagetype.do"
			data-path="ytbank/creditInvestigate/carMortgagetype" onclick="loadForm(this)">汽车按揭类型信息</button>
	<button class="YDYX PP14" data-url="compEdFpPlan/compEdFpPlan.do"
			data-path="ytbank/creditInvestigate/compEdFpPlan" onclick="loadForm(this)">担保公司额度分配方案</button>
	<button class="YDYX PP14" data-url="sameBussEdFp/sameBussEdFp.do"
			data-path="ytbank/creditInvestigate/sameBussEdFp" onclick="loadForm(this)">同业授信额度分配</button>
	<button class="YDYX PP14" data-url="fallBank/fallBank.do"
			data-path="ytbank/creditInvestigate/fallBank" onclick="loadForm(this)">退回上一步</button>
	<button class="YDYX PP14" data-url="isOrNotfallBank/isOrNotfallBank.do"
			data-path="ytbank/creditInvestigate/isOrNotfallBank" onclick="loadForm(this)">是否显示退回上一步</button>
	<button class="YDYX PP14" data-url="eduFound/eduFound.do"
			data-path="ytbank/creditInvestigate/eduFound" onclick="loadForm(this)">额度使用查询（非集团）</button>
	<button class="YDYX PP14" data-url="mobileCrm/mobileCrm.do"
			data-path="db/mobileCrm/mobileCrm" onclick="loadForm(this)">移动crm</button>
	<button class="YDYX PP14" data-url="mobileCrmXw/mobileCrmXw.do"
			data-path="db/mobileCrm/mobileCrmXw" onclick="loadForm(this)">移动crm小微</button>
	<button class="YDYX PP14" data-url="mobileCrmXwPic/mobileCrmXwPic.do"
			data-path="db/mobileCrm/mobileCrmXwPic" onclick="loadForm(this)">移动crm小微趋势图</button>
	<button class="YDYX PP14" data-url="mobileCrm/mobileCrmTaskwork.do"
			data-path="db/mobileCrm/mobileCrmTaskwork" onclick="loadForm(this)">移动crm工作任务</button>
	<button class="YDYX PP14" data-url="mxd/loanList.do"
			data-path="db/market/mxd/loanList" onclick="loadForm(this)">贷款记录查询</button>
	<button class="YDYX PP14" data-url="mxd/products.do"
			data-path="db/market/mxd/products" onclick="loadForm(this)">查询产品列表</button>
	<button class="YDYX PP14" data-url="mxd/users.do"
			data-path="db/market/mxd/users" onclick="loadForm(this)">查询人员列表</button>
	<button class="YDYX PP14" data-url="mxd/caseApply.do"
			data-path="db/market/mxd/caseApply" onclick="loadForm(this)">贷款申请</button>
	<button class="YDYX PP14" data-url="mxd/cases.do"
			data-path="db/market/mxd/cases" onclick="loadForm(this)">案件查询</button>
	<button class="YDYX PP14" data-url="mxd/customerVisit.do"
			data-path="db/market/mxd/customerVisit" onclick="loadForm(this)">客户拜访</button>
	<button class="YDYX PP14" data-url="mxd/visits.do"
			data-path="db/market/mxd/visits" onclick="loadForm(this)">拜访查询</button>
	<button class="YDYX PP14" data-url="loanApplicationProgress/qry.do"
			data-path="db/creditInvestigate/loanApplicationProgress" onclick="loadForm(this)">贷款申请进度列表查询</button>
	<button class="YDYX PP14" data-url="loanApplicationDetail/qry.do"
			data-path="db/creditInvestigate/loanApplicationDetail" onclick="loadForm(this)">贷款申请进度详情查询</button>
	<button class="YDYX PP14" data-url="earlyWarningQuery/qry.do"
			data-path="db/creditInvestigate/00080000200003" onclick="loadForm(this)">预警事项查询</button>
	<button class="YDYX PP14" data-url="customeRiskquery/qry.do"
			data-path="db/creditInvestigate/00080000200004" onclick="loadForm(this)">客户风险查询</button>
	<button class="YDYX PP14" data-url="customeListQuery/qry.do"
			data-path="db/creditInvestigate/00080000200005" onclick="loadForm(this)">客户列表查询</button>
	<!-- PP15 智慧厅堂 -->
	<!-- <button class="YDYX PP15" data-url="mtt/preFormType/formTypeList.do"
			data-path="db/market/mtt/preForm/formTypeList" onclick="loadForm(this)">预填类型列表查询</button>
	<button class="YDYX PP15" data-url="mtt/preForm/ytdManager.do"
			data-path="db/market/mtt/preForm/ytdManager" onclick="loadForm(this)">预填单查询</button>
			
	<button class="YDYX PP15" data-url="mtt/wisdomHall/busiQueuingInfo/busiQueuingInfo.do"
			data-path="db/market/mtt/wisdomHallData/busiQueuingInfo" onclick="loadForm(this)">查询业务队列</button>
	<button class="YDYX PP15" data-url="mtt/wisdomHall/latestInfo/latestInfo.do"
			data-path="db/market/mtt/wisdomHallData/latestInfo" onclick="loadForm(this)">查询厅堂最新消息</button>
	<button class="YDYX PP15" data-url="mtt/wisdomHall/warnningInfo/warnningInfo.do"
			data-path="db/market/mtt/wisdomHallData/warnningInfo" onclick="loadForm(this)">查询预警信息</button>
	<button class="YDYX PP15" data-url="mtt/wisdomHall/queryCustInfo/queryCustInfo.do"
			data-path="db/market/mtt/wisdomHallData/queryCustInfo" onclick="loadForm(this)">查询客户信息</button>
	<button class="YDYX PP15" data-url="mtt/wisdomHall/shiftNo/shiftNo.do"
			data-path="db/market/mtt/wisdomHallData/shiftNo" onclick="loadForm(this)">移号</button>
	<button class="YDYX PP15" data-url="mtt/wisdomHall/abandonNo/abandonNo.do"
			data-path="db/market/mtt/wisdomHallData/abandonNo" onclick="loadForm(this)">弃号</button>
	<button class="YDYX PP15" data-url="mtt/wisdomHall/invedNoAct/invedNoAct.do"
			data-path="db/market/mtt/wisdomHallData/invedNoAct" onclick="loadForm(this)">过号激活</button>
	<button class="YDYX PP15" data-url="mtt/wisdomHall/queryWindowsList/queryWindowsList.do"
			data-path="db/market/mtt/wisdomHallData/queryWindowsList" onclick="loadForm(this)">查询窗口列表</button>
	<button class="YDYX PP15" data-url="mtt/wisdomHall/changeWinPriority/changeWinPriority.do"
			data-path="db/market/mtt/wisdomHallData/changeWinPriority" onclick="loadForm(this)">窗口优先级调整</button>
	<button class="YDYX PP15" data-url="mtt/wisdomHall/referral/referral.do"
			data-path="db/market/mtt/wisdomHallData/referral" onclick="loadForm(this)">转介</button>
	<button class="YDYX PP15" data-url="mtt/wisdomHall/referralInfo/queryReferralInfo.do"
			data-path="db/market/mtt/wisdomHallData/queryReferralInfo" onclick="loadForm(this)">转介查询</button>
	<button class="YDYX PP15" data-url="mtt/wisdomHall/referralOperation/referralOperation.do"
			data-path="db/market/mtt/wisdomHallData/referralOperation" onclick="loadForm(this)">转介操作</button>
	<button class="YDYX PP15" data-url="mtt/wisdomHall/addIntention/addIntention.do"
			data-path="db/market/mtt/wisdomHallData/addIntention" onclick="loadForm(this)">意向添加</button>
	<button class="YDYX PP15" data-url="mtt/wisdomHall/queryIntention/queryIntention.do"
			data-path="db/market/mtt/wisdomHallData/queryIntention" onclick="loadForm(this)">意向查询</button>

	PP16 开卡接口
	<button class="YDYX PP13" data-url="jjk/opencard/queryBusiCount.do"
			data-path="db/market/jjk/tranDeCard/QueryBusiCount" onclick="loadForm(this)">查看业务审核通知数</button>
			
	<button class="YDYX PP16" data-url="jjk/opencard/TranDeCardAdd.do"
			data-path="db/market/jjk/tranDeCard/JjkTranCardAdd" onclick="loadForm(this)">实时开卡提交接口</button>
			
	<button class="YDYX PP16" data-url="jjk/opencard/attaUpload.do"
			data-path="" onclick="loadForm(this)">开卡图片上传</button>
					
	<button class="YDYX PP16" data-url="mm/mgg/message/GetSendCode.do"
			data-path="db/market/mm/mgg/message/GetSendCode" onclick="loadForm(this)">获取短信验证码【挡板】</button>
	<button class="YDYX PP16" data-url="mm/mgg/message/CheckSendCode.do"
			data-path="db/market/mm/mgg/message/CheckSendCode" onclick="loadForm(this)">短信验证码校验【挡板】</button>
			
			
	<button class="YDYX PP16" data-url="jjk/openCard/list/QueryCustSignInfo.do"
			data-path="db/market/jjk/openCard/QueryCustSignInfo" onclick="loadForm(this)">查询客户签约信息【挡板】</button>
	<button class="YDYX PP16" data-url="jjk/openCard/list/OnlineCheck.do"
			data-path="db/market/jjk/openCard/OnlineCheck" onclick="loadForm(this)">联网核查【挡板】</button>
	<button class="YDYX PP16" data-url="jjk/openCard/min/CheckMiniBussiCardId.do"
			data-path="db/market/jjkopenCard/CheckMiniBussiCardId" onclick="loadForm(this)">最小尾箱号查询(包括工具尾箱号)【挡板】</button>
	<button class="YDYX PP16"	data-url="jjk/openCard/list/CheckBussiCardPassword.do"
			data-path="db/market/jjk/openCard/CheckBussiCardPassword"
			onclick="loadForm(this)">验证银行卡密码【挡板】</button>
	<button class="YDYX PP16"	data-url="jjk/openCard/list/CheckBussiCardStatus.do"
			data-path="db/market/jjk/openCard/CheckBussiCardStatus" onclick="loadForm(this)">查询银行卡状态【挡板】</button>
	<button class="YDYX PP16" data-url="jjk/openCard/add/CardInfoAdd2.do"
			data-path="db/market/jjk/openCard/CardInfoAdd2" onclick="loadForm(this)">开卡提交</button>
			
	<button class="YDYX PP16" data-url="jjk/transData/next/transData.do"
			data-path="db/market/jjk/transData/TransData" onclick="loadForm(this)">进度查询统一入口（开卡+信贷）</button>
	<button class="YDYX PP16" data-url="jjk/transData/data/transDataDetail.do"
			data-path="db/market/jjk/transData/TransDataDetail" onclick="loadForm(this)">进度详情查询统一入口（开卡+信贷）</button>

	pp02 基础服务
	<button class="YDYX PP16" data-url="jjk/trandecard/cardInfoAdd.do"
		data-path="db/market/jjk/debit/CardInfoAdd" onclick="loadForm(this)">开卡提交接口【old】</button>
	<button class="YDYX PP16" data-url="json/trandecard/selfBusiQuery.do"
		data-path="db/market/jjk/debit/SelfBusiQuery" onclick="loadForm(this)">客户业务查询接口【old】</button> -->
		<!--pp15 联盟ESB  -->
	<button class="ARES PP15" data-url="ares/getMenus.do"
			data-path="db/menu/menu" onclick="loadForm(this)">获取移动金融菜单</button>
	<button class="ARES PP15" data-url="esb/tailBoxBalanceQuery.do"
			data-path="db/esb/tailBoxBalanceQuery" onclick="loadForm(this)">尾箱余额查询</button>
	
	<button class="ARES PP20" data-url="ares/db/prodList.do"
			data-path="db/ares/prod/prodList" onclick="loadForm(this)">产品列表</button>
	<button class="ARES PP20" data-url="ares/db/queryProd.do"
			data-path="db/ares/prod/queryProd" onclick="loadForm(this)">产品详情</button>
			
	<button class="YDYX PP21" data-url="mm/mzj/propa/list/propaDataList.do"
		data-path="db/market/mm/mzj/propaDataList" onclick="loadForm(this)">宣传展架</button> 
		
	<button class="YDYX PP21" data-url="mm/mzj/propa/list/bannerDataList.do"
		data-path="db/market/mm/mzj/bannerDataList" onclick="loadForm(this)">banner广告</button> 
		
<!-- 	<button class="YDYX PP21" data-url="risk/riskBusiQuery.do"
		data-path="esb/risk/riskBusiQuery" onclick="loadForm(this)">预警事项查询</button>  -->
		
	<button class="YDYX PP21" data-url="risk/riskInfoQuery.do"
		data-path="esb/risk/riskInfoQuery" onclick="loadForm(this)">预警查询</button> 
		
	<button class="YDYX PP21" data-url="credit/custMngLoaner.do"
		data-path="db/credit/custMngLoaner" onclick="loadForm(this)">客户管理</button> 
		
	<button class="YDYX PP21" data-url="credit/loanExpired.do"
		data-path="ytbank/credit/loanExpired" onclick="loadForm(this)">贷款到期提醒</button> 
		
	<button class="YDYX PP21" data-url="credit/loanOverdue.do"
		data-path="ytbank/credit/loanOverdue" onclick="loadForm(this)">贷款逾期提醒</button> 
		
	<button class="YDYX PP21" data-url="credit/loanOvdueRepay.do"
		data-path="ytbank/credit/loanOvdueRepay" onclick="loadForm(this)">逾期还款提醒</button> 
		
	<button class="YDYX PP21" data-url="credit/creditBusiProgQuery.do"
		data-path="ytbank/credit/creditBusiProgQuery" onclick="loadForm(this)">客户经理名下业务进度查询</button> 
		
	<button class="YDYX PP21" data-url="credit/creditBusiProgDetailQuery.do"
		data-path="ytbank/credit/creditBusiProgDetailQuery" onclick="loadForm(this)">进度查询详情</button> 
	<button id="ws" class="YDYX PP22" data-url="ws://WEBSOCKET_IP:${WEBSOCKET_PORT }/websocket/$userId"
		data-path="db/websocket/communication" onclick="loadForm(this)">webSocket接口</button>

	<button class="YDYX PP23" data-url="crm/queryGSkhzbKpi.do"
			data-path="ytbank/crm/queryGSkhzbKpi" onclick="loadForm(this)">零售客户查询</button>
	<button class="YDYX PP23" data-url="crm/queryCustStatusSum.do"
			data-path="ytbank/crm/queryCustStatusSum" onclick="loadForm(this)">客户总数（支行情况）查询</button>
	<button class="YDYX PP23" data-url="crm/queryCustSum.do"
			data-path="ytbank/crm/queryCustSum" onclick="loadForm(this)">客户总数（支行下客户经理）查询</button>
	<button class="YDYX PP23" data-url="crm/querySysNotices.do"
			data-path="ytbank/crm/querySysNotices" onclick="loadForm(this)">全部信息</button>
	<button class="YDYX PP23" data-url="crm/queryNoticseDetails.do"
			data-path="ytbank/crm/queryNoticseDetails" onclick="loadForm(this)">全部信息公告详情</button>
	<button class="YDYX PP23" data-url="crm/queryNoticeMessageList.do"
			data-path="ytbank/crm/queryNoticeMessageList" onclick="loadForm(this)">通知消息</button>
	<button class="YDYX PP23" data-url="crm/queryNoticeMessage.do"
			data-path="ytbank/crm/queryNoticeMessage" onclick="loadForm(this)">通知信息公告详情</button>
	<button class="YDYX PP23" data-url="crm/queryNewsList.do"
			data-path="ytbank/crm/queryNewsList" onclick="loadForm(this)">新闻信息</button>
	<button class="YDYX PP23" data-url="crm/queryNews.do"
			data-path="ytbank/crm/queryNews" onclick="loadForm(this)">新闻信息公告详情</button>
	<button class="YDYX PP23" data-url="crm/queryZxList.do"
			data-path="ytbank/crm/queryZxList" onclick="loadForm(this)">资讯信息</button>
	<button class="YDYX PP23" data-url="crm/queryZx.do"
			data-path="ytbank/crm/queryZx" onclick="loadForm(this)">资讯信息公告详情</button>
	<button class="YDYX PP23" data-url="crm/queryTaskwork.do"
			data-path="ytbank/crm/queryTaskwork" onclick="loadForm(this)">工作任务(客户经理)</button>
	<button class="YDYX PP23" data-url="crm/queryTaskworkList.do"
			data-path="ytbank/crm/queryTaskworkList" onclick="loadForm(this)">工作任务详细信息</button>
	<button class="YDYX PP23" data-url="crm/insertTaskWork.do"
			data-path="ytbank/crm/insertTaskWork" onclick="loadForm(this)">工作任务详细信息保存</button>
	<button class="YDYX PP23" data-url="crm/queryCustVisit.do"
			data-path="ytbank/crm/queryCustVisit" onclick="loadForm(this)">客户访谈详细信息（工作任务下）</button>
	<button class="YDYX PP23" data-url="crm/insertCustVisit.do"
			data-path="ytbank/crm/insertCustVisit" onclick="loadForm(this)">客户访谈详细信息保存</button>
	<button class="YDYX PP23" data-url="crm/queryByCustId.do"
			data-path="ytbank/crm/queryByCustId" onclick="loadForm(this)">客户个人资料（客户访谈下）</button>
	<button class="YDYX PP23" data-url="crm/queryCustAsset.do"
			data-path="ytbank/crm/queryCustAsset" onclick="loadForm(this)">资产结构图（客户个人资料下）</button>
	<button class="YDYX PP23" data-url="crm/queryAddCustVisit.do"
			data-path="ytbank/crm/queryAddCustVisit" onclick="loadForm(this)">客户访谈（客户个人资料下）</button>
	<button class="YDYX PP23" data-url="crm/insertCustVisit.do"
			data-path="ytbank/crm/insertCustVisit" onclick="loadForm(this)">添加客户访谈信息</button>
	<button class="YDYX PP23" data-url="crm/queryCustVisitList.do"
			data-path="ytbank/crm/queryCustVisitList" onclick="loadForm(this)">客户访谈下的客户详细信息</button>
	<button class="YDYX PP23" data-url="crm/queryKhqrList.do"
			data-path="ytbank/crm/queryKhqrList" onclick="loadForm(this)">客户确认列表信息</button>
	<button class="YDYX PP23" data-url="crm/queryKhqrListOperate.do"
			data-path="ytbank/crm/queryKhqrListOperate" onclick="loadForm(this)">客户信息操作（客户确认下）</button>
	<button class="YDYX PP23" data-url="crm/queryDbApproveList.do"
			data-path="ytbank/crm/queryDbApproveList" onclick="loadForm(this)">待办审批列表信息</button>
	<button class="YDYX PP23" data-url="crm/queryRLList.do"
			data-path="ytbank/crm/queryRLList" onclick="loadForm(this)">客户认领审批列表信息（待办审批下）</button>
	<button class="YDYX PP23" data-url="crm/queryClaimCust.do"
			data-path="ytbank/crm/queryClaimCust" onclick="loadForm(this)">客户认领审批下客户详细信息</button>
	<button class="YDYX PP23" data-url="crm/insertClaimOpinion.do"
			data-path="ytbank/crm/insertClaimOpinion" onclick="loadForm(this)">客户认领审批下审批意见</button>
	<button class="YDYX PP23" data-url="crm/queryTurnCustList.do"
			data-path="ytbank/crm/queryTurnCustList" onclick="loadForm(this)">客户移交审批列表信息（待办审批下）</button>
	<button class="YDYX PP23" data-url="crm/query.do"
			data-path="ytbank/crm/query" onclick="loadForm(this)">客户移交审批下客户详细信息</button>
	<button class="YDYX PP23" data-url="crm/insertTurnOpinion.do"
			data-path="ytbank/crm/insertTurnOpinion" onclick="loadForm(this)">客户移交审批下审批意见</button>
	<button class="YDYX PP23" data-url="crm/queryChangeCustList.do"
			data-path="ytbank/crm/queryChangeCustList" onclick="loadForm(this)">等级变动审批列表信息（待办审批下）</button>
	<button class="YDYX PP23" data-url="crm/queryChangeCust.do"
			data-path="ytbank/crm/queryChangeCust" onclick="loadForm(this)">等级变动审批下客户详细信息</button>
	<button class="YDYX PP23" data-url="crm/insertChangeCust.do"
			data-path="ytbank/crm/insertChangeCust" onclick="loadForm(this)">等级变动审批下审批意见</button>
	<button class="YDYX PP23" data-url="crm/queryBackCustList.do"
			data-path="ytbank/crm/queryBackCustList" onclick="loadForm(this)">客户退回审批列表信息（待办审批下）</button>
	<button class="YDYX PP23" data-url="crm/queryBackCust.do"
			data-path="ytbank/crm/queryBackCust" onclick="loadForm(this)">客户退回审批下客户详细信息</button>
	<button class="YDYX PP23" data-url="crm/insertBackCust.do"
			data-path="ytbank/crm/insertBackCust" onclick="loadForm(this)">客户退回审批下审批意见</button>
	<button class="YDYX PP23" data-url="crm/queryzjyd.do"
			data-path="ytbank/crm/queryzjyd" onclick="loadForm(this)">资金异动</button>
	<button class="YDYX PP23" data-url="crm/queryUntreatedCust.do"
			data-path="ytbank/crm/queryUntreatedCust" onclick="loadForm(this)">资金异动下的未处理客户</button>
	<button class="YDYX PP23" data-url="crm/insertUntreatedCust.do"
			data-path="ytbank/crm/insertUntreatedCust" onclick="loadForm(this)">资金异动下的未处理客户处理意见</button>
	<button class="YDYX PP23" data-url="crm/queryProcessedCust.do"
			data-path="ytbank/crm/queryProcessedCust" onclick="loadForm(this)">资金异动下的已处理客户</button>
	<button class="YDYX PP23" data-url="crm/queryqtsj.do"
			data-path="ytbank/crm/queryqtsj" onclick="loadForm(this)">其他事件</button>
	<button class="YDYX PP23" data-url="crm/querysqjd.do"
			data-path="ytbank/crm/querysqjd" onclick="loadForm(this)">申请进度</button>
	<button class="YDYX PP23" data-url="crm/querysqjdView.do"
			data-path="ytbank/crm/querysqjdView" onclick="loadForm(this)">申请进度（待审核）</button>
	<button class="YDYX PP23" data-url="crm/querysqjdViewPass.do"
			data-path="ytbank/crm/querysqjdViewPass" onclick="loadForm(this)">申请进度（审核通过）</button>
	<button class="YDYX PP23" data-url="crm/querykhgz.do"
			data-path="ytbank/crm/querykhgz" onclick="loadForm(this)">客户关注</button>
	<button class="YDYX PP23" data-url="crm/querykhgh.do"
			data-path="ytbank/crm/querykhgh" onclick="loadForm(this)">客户关怀</button>
	<button class="YDYX PP23" data-url="crm/queryUntreatedVisit.do"
			data-path="ytbank/crm/queryUntreatedVisit" onclick="loadForm(this)">客户访谈（客户关怀下未处理客户）</button>
	<button class="YDYX PP23" data-url="crm/insertUntreatedVisit.do"
			data-path="ytbank/crm/insertUntreatedVisit" onclick="loadForm(this)">客户访谈详细信息保存</button>
	<button class="YDYX PP23" data-url="crm/queryProcessedVisit.do"
			data-path="ytbank/crm/queryProcessedVisit" onclick="loadForm(this)">客户关怀下的已处理客户</button>
	<button class="YDYX PP23" data-url="crm/queryManageTaskWork.do"
			data-path="ytbank/crm/queryManageTaskWork" onclick="loadForm(this)">工作任务</button>
	<button class="YDYX PP23" data-url="crm/queryBigBranchTaskWork.do"
			data-path="ytbank/crm/queryBigBranchTaskWork" onclick="loadForm(this)">工作任务大支行详细信息</button>
	<button class="YDYX PP23" data-url="crm/querySmallBranchTaskWork.do"
			data-path="ytbank/crm/querySmallBranchTaskWork" onclick="loadForm(this)">工作任务大支行下小支行详细信息</button>
	<button class="YDYX PP23" data-url="crm/queryCustBranchTaskWork.do"
			data-path="ytbank/crm/queryCustBranchTaskWork" onclick="loadForm(this)">工作任务小支行下经理详细信息</button>
	<button class="YDYX PP23" data-url="crm/queryManagezjyd.do"
			data-path="ytbank/crm/queryManagezjyd" onclick="loadForm(this)">资金异动</button>
	<button class="YDYX PP23" data-url="crm/queryMarketingTask.do"
			data-path="ytbank/crm/queryMarketingTask" onclick="loadForm(this)">营销任务</button>
	<button class="YDYX PP23" data-url="crm/queryMarketingTaskCase.do"
			data-path="ytbank/crm/queryMarketingTaskCase" onclick="loadForm(this)">任务完成情况（营销任务下）</button>
	<button class="YDYX PP23" data-url="crm/queryManageQTSJ.do"
			data-path="ytbank/crm/queryManageQTSJ" onclick="loadForm(this)">其他事件</button>
	<button class="YDYX PP23" data-url="crm/queryBigBranchQTSJ.do"
			data-path="ytbank/crm/queryBigBranchQTSJ" onclick="loadForm(this)">其他事件大支行详细信息</button>
	<button class="YDYX PP23" data-url="crm/querySmallBranchQTSJ.do"
			data-path="ytbank/crm/querySmallBranchQTSJ" onclick="loadForm(this)">其他事件大支行下小支行详细信息</button>
	<button class="YDYX PP23" data-url="crm/queryManageKHGH.do"
			data-path="ytbank/crm/queryManageKHGH" onclick="loadForm(this)">客户关怀</button>
	<button class="YDYX PP23" data-url="crm/queryBigBranchKHGH.do"
			data-path="ytbank/crm/queryBigBranchKHGH" onclick="loadForm(this)">客户关怀大支行详细信息</button>
	<button class="YDYX PP23" data-url="crm/querySmallBranchKHGH.do"
			data-path="ytbank/crm/querySmallBranchKHGH" onclick="loadForm(this)">客户关怀大支行下小支行详细信息</button>
	<button class="YDYX PP23" data-url="crm/queryManageDQYJ.do"
			data-path="ytbank/crm/queryManageDQYJ" onclick="loadForm(this)">到期预警</button>
	<button class="YDYX PP23" data-url="crm/queryBigBranchDQYJ.do"
			data-path="ytbank/crm/queryBigBranchDQYJ" onclick="loadForm(this)">到期预警大支行详细信息</button>
	<button class="YDYX PP23" data-url="crm/querySmallBranchDQYJ.do"
			data-path="ytbank/crm/querySmallBranchDQYJ" onclick="loadForm(this)">到期预警大支行下小支行详细信息</button>
	<button class="YDYX PP23" data-url="crm/queryManageKHGZ.do"
			data-path="ytbank/crm/queryManageKHGZ" onclick="loadForm(this)">客户关注</button>
	<button class="YDYX PP23" data-url="crm/queryBigBranchKHGZ.do"
			data-path="ytbank/crm/queryBigBranchKHGZ" onclick="loadForm(this)">客户关注大支行详细信息</button>
	<button class="YDYX PP23" data-url="crm/querySmallBranchKHGZ.do"
			data-path="ytbank/crm/querySmallBranchKHGZ" onclick="loadForm(this)">客户关注大支行下小支行详细信息</button>
	<button class="YDYX PP23" data-url="crm/queryPersonDQYJ.do"
			data-path="ytbank/crm/queryPersonDQYJ" onclick="loadForm(this)">到期预警(个人平台)</button>
	<button class="YDYX PP23" data-url="crm/queryPersonKHFT.do"
			data-path="ytbank/crm/queryPersonKHFT" onclick="loadForm(this)">客户访谈（到期预警未处理）</button>
	<button class="YDYX PP23" data-url="crm/insertPersonKHFT.do"
			data-path="ytbank/crm/insertPersonKHFT" onclick="loadForm(this)">客户访谈保存（到期预警未处理）</button>
	<button class="YDYX PP23" data-url="crm/queryProcessedKHFT.do"
			data-path="ytbank/crm/queryProcessedKHFT" onclick="loadForm(this)">客户访谈（到期预警已处理）</button>
	<button class="YDYX PP23" data-url="crm/queryMyMarketingTask.do"
			data-path="ytbank/crm/queryMyMarketingTask" onclick="loadForm(this)">我的营销任务</button>
	<button class="YDYX PP23" data-url="crm/queryCustMarketingTask.do"
			data-path="ytbank/crm/queryCustMarketingTask" onclick="loadForm(this)">任务明细（我的营销任务下）</button>
	<button class="YDYX PP23" data-url="crm/querymycust.do"
			data-path="ytbank/crm/querymycust" onclick="loadForm(this)">我的客户</button>
	<button class="YDYX PP23" data-url="crm/updateOperateCust.do"
			data-path="ytbank/crm/updateOperateCust" onclick="loadForm(this)">操作（我的客户下）</button>
	<button class="YDYX PP23" data-url="crm/queryMarketingCampaign.do"
			data-path="ytbank/crm/queryMarketingCampaign" onclick="loadForm(this)">营销活动</button>
	<button class="YDYX PP23" data-url="crm/queryMarketingCampaignList.do"
			data-path="ytbank/crm/queryMarketingCampaignList" onclick="loadForm(this)">营销活动详情</button>
	<button class="YDYX PP23" data-url="crm/queryMarketingCampaignApply.do"
			data-path="ytbank/crm/queryMarketingCampaignApply" onclick="loadForm(this)">营销活动报名</button>
	<button class="YDYX PP23" data-url="crm/updateMarketingCampaign.do"
			data-path="ytbank/crm/updateMarketingCampaign" onclick="loadForm(this)">营销活动报名状态更改</button>
	<button class="YDYX PP23" data-url="crm/queryClientBase.do"
			data-path="ytbank/crm/queryClientBase" onclick="loadForm(this)">客户群</button>
	<button class="YDYX PP23" data-url="crm/queryeditGroCust.do"
			data-path="ytbank/crm/queryeditGroCust" onclick="loadForm(this)">单个群信息</button>
	<button class="YDYX PP23" data-url="crm/insertOperateMyCust.do"
			data-path="ytbank/crm/insertOperateMyCust" onclick="loadForm(this)">操作（在新增客户的我的客户下）</button>
	<button class="YDYX PP23" data-url="crm/insertEditGroCust.do"
			data-path="ytbank/crm/insertEditGroCust" onclick="loadForm(this)">增加行外客户（在新增客户页面下）</button>
	<button class="YDYX PP23" data-url="crm/queryMyManage.do"
			data-path="ytbank/crm/queryMyManage" onclick="loadForm(this)">我的</button>
	<button class="YDYX PP23" data-url="crm/updatePassword.do"
			data-path="ytbank/crm/updatePassword" onclick="loadForm(this)">密码修改</button>
	<button class="YDYX PP23" data-url="crm/queryAboutUs.do"
			data-path="ytbank/crm/queryAboutUs" onclick="loadForm(this)">关于我们</button>
	<button class="YDYX PP23" data-url="crm/queryWorkCalendar .do"
			data-path="ytbank/crm/queryWorkCalendar" onclick="loadForm(this)">工作日历</button>
	<button class="YDYX PP23" data-url="crm/queryWorkCalendarList.do"
			data-path="ytbank/crm/queryWorkCalendarList " onclick="loadForm(this)">日历详情</button>
	<button class="YDYX PP23" data-url="crm/updateHeadPortrait.do"
			data-path="ytbank/crm/updateHeadPortrait " onclick="loadForm(this)">修改头像</button>
	<button class="YDYX PP23" data-url="crm/queryFiles.do"
			data-path="ytbank/crm/queryFiles" onclick="loadForm(this)">附件</button>
	<button class="YDYX PP23" data-url="crm/queryManagezjydQuery.do"
			data-path="ytbank/crm/queryManagezjydQuery" onclick="loadForm(this)">资金异动（查询任务）</button>
	<button class="YDYX PP23" data-url="crm/queryBalance.do"
			data-path="ytbank/crm/queryBalance" onclick="loadForm(this)">个人金融资产余额</button>
	<button class="YDYX PP23" data-url="crm/queryCustBranchQTSJ.do"
			data-path="ytbank/crm/queryCustBranchQTSJ" onclick="loadForm(this)">其他事件小支行下经理详细信息</button>
	<button class="YDYX PP23" data-url="crm/queryCustBranchKHGH.do"
			data-path="ytbank/crm/queryCustBranchKHGH" onclick="loadForm(this)">客户关怀小支行下经理详细信息</button>
	<button class="YDYX PP23" data-url="crm/queryCustBranchKHGZ.do"
			data-path="ytbank/crm/queryCustBranchKHGZ" onclick="loadForm(this)">客户关注小支行下经理详细信息</button>
	<button class="YDYX PP23" data-url="crm/queryAddPersonCust.do"
			data-path="ytbank/crm/queryAddPersonCust" onclick="loadForm(this)">新增客户（在单个群下）</button>
	<button class="YDYX PP23" data-url="crm/insertGesture.do"
			data-path="ytbank/crm/insertGesture" onclick="loadForm(this)">手势登陆</button>
	<button class="YDYX PP23" data-url="crm/insertFingerprint.do"
			data-path="ytbank/crm/insertFingerprint" onclick="loadForm(this)">指纹登陆</button>
</body>
<script type="text/javascript">
	//$('#ws').setAttribute('data-url',$('#ws').getAttribute('data-url').replace(/WEBSOCKET_IP/,location.hostname));
	$('#ws').attr('data-url',"ws://"+location.hostname+":${WEBSOCKET_PORT }${pageContext.request.contextPath}/websocket/$userId");

</script>
</html>
