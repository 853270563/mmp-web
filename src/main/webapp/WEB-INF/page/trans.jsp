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
			data-path="ares/login/ClientNoLogin" onclick="loadForm(this)">无会话登录交易</button>
	<button class="ARES PP01" data-url="ares/login/ClientLogin.do"
		data-path="ares/login/ClientLogin" onclick="loadForm(this)">登录交易</button>
	<button class="ARES PP01" data-url="ares/login/ClientLogout.do"
		data-path="ares/login/ClientLogout" onclick="loadForm(this)">安全退出</button>
	<button class="ARES PP01" data-url="ares/login/loginExpandData.do"
		data-path="ares/login/loginExpandData" onclick="loadForm(this)">登录拓展数据管理</button>
	<button class="ARES PP01" data-url="ares/login/ChangePwd.do"
		data-path="ares/login/ChangePwd" onclick="loadForm(this)">修改密码</button>
	<button class="ARES PP01" data-url="ares/login/BusiLogAdd.do"
			data-path="ares/login/BusiLog" onclick="loadForm(this)">日志提交</button>
	<button class="ARES PP01" data-url="ares/login/NetworkHeartbeat.do"
			data-path="ares/login/NetworkHeartbeat" onclick="loadForm(this)">网络心跳</button>
	<button class="ARES PP01" data-url="ares/login/gesturePwdCheck.do"
			data-path="ares/login/GesturePwdCheck" onclick="loadForm(this)">登录密码校验（手势密码用）</button>
	<button class="ARES PP01" data-url="ares/login/imageValidateCode.do"
			data-path="ares/login/imageValidateCode" onclick="loadForm(this)">图片验证码</button>
	<button class="ARES PP01" data-url="common/deviceCrash/saveCrashLog.do"
			data-path="common/deviceCrash/saveCrashLog" onclick="loadForm(this)">设备崩溃日志上传</button>

	<!-- PP02 应用管理接口 -->
	<button class="ARES PP02" data-url="ares/appManage/getApkUpdateInfo.do"
			data-path="ares/appManage/getApkUpdateInfo" onclick="loadForm(this)">原生资源更新</button>
	<button class="ARES PP02" data-url="ares/appManage/getHtmlUpdateInfo.do"
			data-path="ares/appManage/getHtmlUpdateInfo" onclick="loadForm(this)">HTML资源更新</button>
	<button class="ARES PP02" data-url="ares/appManage/fileDownLoad.do"
			data-path="ares/appManage/fileDownLoad" onclick="loadForm(this)">资源包下载</button>
	<button class="ARES PP02" data-url="ares/appManage/oneFileDownLoad.do"
			data-path="ares/appManage/fileDownLoad" onclick="loadForm(this)">单文件下载</button>
	<button class="ARES PP02" data-url="ares/appManage/roleApp.do"
			data-path="ares/appManage/roleApp" onclick="loadForm(this)">用户可下载应用</button>
	<button class="ARES PP02" data-url="ares/appManage/saveAppVers.do"
			data-path="ares/appManage/saveAppVers" onclick="loadForm(this)">资源包上传（第三方）</button>

	<!-- PP03 信息服务 -->
	<button class="ARES PP03" data-url="system/note/loadNote.do"
			data-path="system/note/loadNote" onclick="loadForm(this)">资讯公告加载</button>
	<button class="ARES PP03" data-url="system/note/noteList.do"
			data-path="system/note/noteList" onclick="loadForm(this)">资讯公告列表</button>
	<button class="ARES PP03" data-url="system/banner/bannerDataList.do"
			data-path="system/banner/bannerDataList" onclick="loadForm(this)">广告banner查询接口</button>
	<button class="ARES PP03" data-url="system/propa/propaDataList.do"
			data-path="system/propa/propaDataList" onclick="loadForm(this)">产品展架查询接口（知识库）</button>
	<button class="ARES PP03" data-url="system/rate/queryRateInfo.do"
			data-path="system/rate/queryRateInfo" onclick="loadForm(this)">查询利率信息（金融行情）</button>
	<button class="ARES PP03" data-url="system/branch/queryBranchList.do"
			data-path="system/branch/queryBranchList" onclick="loadForm(this)">查询网点信息</button>

	<!-- PP04 文件上传 -->
	<button class="ARES PP04" data-url="ares/file/upload.do"
			data-path="" onclick="loadForm(this)">开户图片上传</button>
	<button class="ARES PP04" data-url="fileUpload/breakpoint/checkFile.do"
			data-path="ares/fileBreakUpload/checkFile" onclick="loadForm(this)">断点续传-检查文件状态</button>
	<button class="ARES PP04" data-url="fileUpload/breakpoint/zipUpload.do"
			data-path="ares/fileBreakUpload/zipUpload" onclick="loadForm(this)">断点续传-文件上传</button>
	<button class="ARES PP04" data-url="ares/file/breakUploadTrans.do"
			data-path="ares/fileBreakUpload/breakUploadTrans" onclick="loadForm(this)">断点续传-上传文件后业务处理</button>

	<!-- PP05 公共服务 -->
			
	<!-- PP07 设备管理 -->
	<button class="ARES PP07" data-url="ares/deviceInfo/boundDevice.do"
			data-path="ares/deviceInfo/boundDevice" onclick="loadForm(this)">设备绑定</button>
	<button class="ARES PP07" data-url="ares/deviceInfo/undoBoundDevice.do"
			data-path="ares/deviceInfo/undoBoundDevice" onclick="loadForm(this)">设备解绑</button>
</body>
</html>
