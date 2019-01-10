/**
 * 测试场景说明, 按字母升序进行执行<br>
 * 场景编号/子顺序号。<br>
 * 单个子顺序目录里面可以包含多个可并发执行的交易，将有依赖关系的交易分拆到不同子顺序号中<br>
 * <code>
 {
	code : "YYM/01",
	desc : "YYM场景描述，登录前",
	trans : [ {
		transCode : "",
		//rtnCodeLabel : "STATUS",//缺省STATUS，也可以提定其它属性值进行比对
		rtnCode : "ERR00222",
		desc : "反案例"
	}]}
</code>
 */

var cases = [ 
{code : "", desc : "请选择测试场景" }
,{code : "YYM/01", desc : "场景描述，登录前" } 
,{code : "YYM/02", desc : "场景描述，登录后交易" } 
,{
	code : "YYM/03",
	desc : "场景描述，登录前反案例",
	trans : [ {
		transCode : "easy/sample2",
		//rtnCodeLabel : "STATUS",//缺省STATUS，也可以提定其它属性值进行比对
		rtnCode : "demo01.0001",
		desc : "反案例"
	}]}
//--A组场景---
//登录
,{code : "HYH/A00/01", desc : "HYH场景描述，静态密码登录" }
,{code : "HYH/A00/02", desc : "HYH场景描述，首次登录开启手势" }
,{code : "HYH/A00/03", desc : "HYH场景描述，首次登录开启指纹" }
,{code : "HYH/A00/04", desc : "HYH场景描述，首次登录重置登录密码" }
,{code : "HYH/A01", desc : "HYH场景描述，静态密码登录" }
,{code : "HYH/A02", desc : "HYH场景描述，手势密码登录" }
,{code : "HYH/A03", desc : "HYH场景描述，指纹密码登录" }
,{code : "HYH/A04", desc : "HYH场景描述，扫码登录" }
,{code : "HYH/A05/01", desc : "HYH场景描述，QQ关联" }
,{code : "HYH/A05/02", desc : "HYH场景描述，WEIBO关联" }
,{code : "HYH/A05/03", desc : "HYH场景描述，WECHAT关联" }
,{code : "HYH/A05/04", desc : "HYH场景描述，关联登录（静态密码）" }
,{code : "HYH/A05/05", desc : "HYH场景描述，安全退出服务" }
//注册
,{code : "HYH/A06/01", desc : "HYH场景描述，模拟发短信" }
,{code : "HYH/A06/02", desc : "HYH场景描述，验证短信验证码" }
,{code : "HYH/A06/03", desc : "HYH场景描述，注册验证手机号" }
,{code : "HYH/A06/04", desc : "HYH场景描述，获取邀请好友列表" }
,{code : "HYH/A06/05", desc : "HYH场景描述，注册" }
,{code : "HYH/A06/06", desc : "HYH场景描述，邀请好友注册" }
,{code : "HYH/A06/07", desc : "HYH场景描述，邀请人列表查询" }
,{code : "HYH/A07", desc : "HYH场景描述，关联注册" }
,{code : "HYH/A08/01", desc : "HYH场景描述，实名认证" }
,{code : "HYH/A08/02", desc : "HYH场景描述，获取下挂卡列表" }
,{code : "HYH/A08/03", desc : "HYH场景描述，插卡BIN" }
,{code : "HYH/A08/04", desc : "HYH场景描述，账户鉴权" }
,{code : "HYH/A08/05", desc : "HYH场景描述，开通Ⅱ类户服务(已下挂借记卡)" }
,{code : "HYH/A08/06", desc : "HYH场景描述，开通Ⅱ类户服务(本人他行借记卡)" }
,{code : "HYH/A08/07", desc : "HYH场景描述，开通Ⅱ类户服务(非下挂本人本行借记卡)" }

//忘记登录密码
,{code : "HYH/A09/01", desc : "HYH场景描述，模拟发短信" }
,{code : "HYH/A09/02", desc : "HYH场景描述，验证短信验证码" }
,{code : "HYH/A09/01", desc : "HYH场景描述，用户信息，获取用户等级，获取是否设置支付密码" }
,{code : "HYH/A09/02", desc : "HYH场景描述，获取安全问题" }
,{code : "HYH/A09/02", desc : "HYH场景描述，重置登录密码" }
//修改登录密码
,{code : "HYH/A10", desc : "HYH场景描述，修改登录密码" }
//修改支付密码
,{code : "HYH/A11/01", desc : "HYH场景描述，验证手机号" }
,{code : "HYH/A11/02", desc : "HYH场景描述，修改支付密码" }
//修改手机号
,{code : "HYH/A12/01", desc : "HYH场景描述，身份验证" }
,{code : "HYH/A12/02", desc : "HYH场景描述，支付密码验证" }
,{code : "HYH/A12/03", desc : "HYH场景描述，修改手机号" }
//实名认证
,{code : "HYH/A13", desc : "HYH场景描述，实名认证" }
//绑定账户
,{code : "HYH/A14/01", desc : "HYH场景描述，电子账号绑定已下挂银行卡服务" }
,{code : "HYH/A14/02", desc : "HYH场景描述，电子账号绑定本人他行银行卡服务" }
,{code : "HYH/A14/03", desc : "HYH场景描述，电子账号绑定非下挂本人本行银行卡服务" }
//解绑
,{code : "HYH/A15", desc : "HYH场景描述，绑定账户解绑" }
//活动列表
,{code : "HYH/A16", desc : "HYH场景描述，附近的活动" }
,{code : "HYH/A17", desc : "HYH场景描述，活动详情" }
,{code : "HYH/A18/01", desc : "HYH场景描述，市区查询" }
,{code : "HYH/A18/02", desc : "HYH场景描述，区域查找" }
//我的活动
,{code : "HYH/A19", desc : "HYH场景描述，我中奖的活动" }
,{code : "HYH/A19/01", desc : "HYH场景描述，填写领奖地址" }
,{code : "HYH/A19/02", desc : "HYH场景描述，查询省市区" }
,{code : "HYH/A11/01", desc : "移动设备绑定维护" }
//安全认证方式查询
,{code : "HYH/A20/01", desc : "用户安全认证方式查询" }
,{code : "HYH/A20/02", desc : "交易可用安全认证方式查询" }
//客户端获取省市区
,{code : "HYH/A21/01", desc : "查询所有省市区" }
//收藏理财产品
,{code : "HYH/A22", desc : "收藏理财产品" }
,{code : "HYH/A23", desc : "删除收藏理财产品" }
,{code : "HYH/A24", desc : "查询收藏理财产品状态" }
,{code : "HYH/A25", desc : "查询收藏理财产品列表" }
//忘记支付密码
,{code : "HYH/A26/01", desc : "忘记支付密码-验证注册手机号" }
,{code : "HYH/A26/02", desc : "忘记支付密码-重置支付密码" }
//传统存款查询
,{code : "HYH/A27/01", desc : "定期存款列表查询" }
,{code : "HYH/A27/02", desc : "定活两便列表查询" }
,{code : "HYH/A27/03", desc : "零存整取列表查询" }
,{code : "HYH/A27/04", desc : "通知存款列表查询" }
//理财超市-风评
,{code : "HYH/A28/01", desc : "风险评估信息查询" }
,{code : "HYH/A28/02", desc : "客户风险评估问题列表查询服务" }
,{code : "HYH/A28/03", desc : "调用客户风险评估服务" }
,{code : "HYH/A28/04", desc : "风险评估确认" }
//理财列表-购买
,{code : "HYH/A29/01", desc : "理财超市列表查询服务" }
,{code : "HYH/A29/02", desc : "理财产品详情查询服务" }
,{code : "HYH/A29/03", desc : "理财产品说明书查询服务" }
,{code : "HYH/A29/04", desc : "理财产品购买服务" }
//我的理财
,{code : "HYH/A30/01", desc : "调用已持有的理财产品查询服务" }
,{code : "HYH/A30/02", desc : "调用我的理财产品总资产查询服务" }
,{code : "HYH/A30/03", desc : "调用我的理财产品昨日收益详情查询" }
,{code : "HYH/A30/04", desc : "调用待处理的理财产品查询服务" }
,{code : "HYH/A30/05", desc : "调用理财产品赎回服务" }
,{code : "HYH/A30/06", desc : "调用撤销理财产品服务" }
,{code : "HYH/A30/07", desc : "调用理财产品交易明细查询服务" }

//-B组场景
,{code : "HZF/transfer/01", desc : "HZF场景描述，转账相关" } 
,{code : "HZF/recvPerson/02", desc : "HZF场景描述，收款人管理相关" }  
,{code : "HZF/redPackage/03", desc : "HZF场景描述，红包相关" }   
,{code : "HZF/epg/04", desc : "HZF场景描述，支付网关" }  
,{code : "HZF/supper/05", desc : "HZF场景描述，超级网银" }  
,{code : "HZF/recharge/06", desc : "HZF场景描述，增值业务" }  

//--C组交易场景
,{code : "WDD/D00", desc : "YYM场景描述，登录前" }
,{code : "WDD/C01/01", desc : "个人信息查询，core/custInfoQuery" }
,{code : "WDD/C02/01", desc : "联系地址查询，core/userAddrQuery" }
,{code : "WDD/C03/01", desc : "指纹支付状态查询，core/fingerStateQuery" }
,{code : "WDD/C03/02", desc : "指纹支付开启，core/fingerPayOpen" }
,{code : "WDD/C03/03", desc : "指纹支付关闭，core/fingerPayClose" }
,{code : "WDD/C04/01", desc : "小额免密开关状态查询，core/dribletStateQuery" }
,{code : "WDD/C04/02", desc : "小额免密开启，core/dribletPwdOpen" }
,{code : "WDD/C04/03", desc : "小额免密关闭，core/dribletPwdClose" }
,{code : "WDD/C05/01", desc : "联系地址查询，core/userAddrQuery" }
,{code : "WDD/C06/01", desc : "指纹登录状态查询，core/fingerPwdStateQuery" }
,{code : "WDD/C06/02", desc : "指纹登录开启，core/fingerPwdOpen" }
,{code : "WDD/C06/03", desc : "指纹登录关闭，core/fingerPwdClose" }
,{code : "WDD/C07/01", desc : "手势密码状态查询，core/gesturePwdStateQuery" }
,{code : "WDD/C07/02", desc : "手势密码开启，core/gesturePwdOpen" }
,{code : "WDD/C07/03", desc : "手势密码关闭，core/gesturePwdClose" }
,{code : "WDD/C08/01", desc : "系统安全问题查询，core/sysSafetyIssueQuery" }
,{code : "WDD/C08/02", desc : "安全问题设置，core/safetyIssueSet" }
,{code : "WDD/C08/03", desc : "安全问题查询，core/secuProblemQuery" }
,{code : "WDD/C08/04", desc : "安全问题修改，core/secuProblemUpdate" }
,{code : "WDD/C08/05", desc : "安全问题重置，core/secuProblemReset" }
,{code : "WDD/C09/01", desc : "下挂账户列表查询，core/acctListQuery" }
,{code : "WDD/C10/01", desc : "下挂账户列表查询，core/acctListQuery" }
,{code : "WDD/C10/02", desc : "账户别名设置，core/acctAliasSet" }
,{code : "WDD/C11/01", desc : "下挂账户列表查询，core/acctListQuery" }
,{code : "WDD/C11/02", desc : "设置默认账户，core/defaultAcctSet" }
,{code : "WDD/C12/01", desc : "下挂账户列表查询，core/acctListQuery" }
,{code : "WDD/C12/02", desc : "下挂账户删除，core/acctRemove" }
,{code : "WDD/C13/01", desc : "客户基本信息查询，core/custMainInfoQuery" }
,{code : "WDD/C13/02", desc : "头像设置，core/custHeadImgSet" }
,{code : "WDD/C14/01", desc : "客户基本信息查询，core/custMainInfoQuery" }
,{code : "WDD/C14/02", desc : "邮箱设置，core/mailSet" }
,{code : "WDD/C15/01", desc : "客户基本信息查询，core/custMainInfoQuery" }
,{code : "WDD/C15/02", desc : "预留信息设置，core/obliInfoSet" }
,{code : "WDD/C16/01", desc : "客户基本信息查询，core/custMainInfoQuery" }
,{code : "WDD/C16/02", desc : "用户名设置，core/userNameSet" }
,{code : "WDD/C17/01", desc : "客户基本信息查询，core/custMainInfoQuery" }
,{code : "WDD/C17/02", desc : "联系地址修改，core/userAddrSet" }
,{code : "WDD/C18/01", desc : "积分账户查询，core/pointAcctQuery" }
,{code : "WDD/C18/02", desc : "积分账户明细，core/pointInfoPageQuery" }
,{code : "WDD/C19/01", desc : "联系地址查询，core/userAddrQuery" }
,{code : "WDD/C19/02", desc : "联系地址创建，core/userAddrCreate" }
,{code : "WDD/C20/01", desc : "电子账户绑定账户列表查询，core/eleAcctBindListQuery" }
,{code : "WDD/C20/02", desc : "电子账户充值，core/eleAcctRecharge" }
,{code : "WDD/C20/03", desc : "电子账户提现，core/eleAcctCashOut" }
,{code : "WDD/C21/01", desc : "下挂账户列表查询，core/acctListQuery" }
,{code : "WDD/C21/02", desc : "借记卡交易明细查询，core/transPageQuery" }
,{code : "WDD/C21/03", desc : "电子账户交易明细查询，core/transPageQuery4EleAcct" }
,{code : "WDD/C22/01", desc : "未下挂账户列表查询，core/unAddAcctListQuery" }
,{code : "WDD/C22/02", desc : "借记卡卡片状态查询，core/acctStateQuery" }
,{code : "WDD/C22/03", desc : "账户添加，core/acctAdd" }
,{code : "WDD/C23/01", desc : "下挂账户列表查询，core/acctListQuery" }
,{code : "WDD/C23/02", desc : "借记卡账户详情查询，core/acctDetailQuery" }
,{code : "WDD/C23/03", desc : "借记卡定期账户列表查询，core/acctDetailQuery" }
,{code : "WDD/C23/04", desc : "电子账户详情查询，core/acctListQuery" }
,{code : "WDD/C23/04", desc : "电子账户锁定，core/eleAcctLock" }
,{code : "WDD/C23/05", desc : "电子账户解锁，core/eleAcctUnlock" }
,{code : "WDD/C23/05", desc : "借记卡口头挂失，core/acctTempLoss" }
,{code : "WDD/C24/01", desc : "下挂账户列表查询，core/acctListQuery" }
,{code : "WDD/C24/02", desc : "动帐通知开通信息查询，core/acctNoticeInfoQuery" }
,{code : "WDD/C24/03", desc : "动帐通知开通，core/acctNoticeOpen" }
,{code : "WDD/C24/04", desc : "动帐通知解约，core/acctNoticeColse" }
,{code : "WDD/C25/01", desc : "下挂账户列表查询，core/acctListQuery" }
,{code : "WDD/C25/02", desc : "查询密码重置，core/acctPwdReset" }
,{code : "WDD/C25/03", desc : "查询密码修改，core/acctPwdSet" }
,{code : "WDD/C25/04", desc : "取款查询密码验证，core/acctDrawPwdverify" }
,{code : "WDD/C26/01", desc : "用户资产负债信息查询，core/userAssetQuery" }
,{code : "WDD/C27/01", desc : "用户非柜面转账签约限额查询，core/noTellerTransSigLimQuery" }
,{code : "WDD/C28/01", desc : "用户非柜面转账签约限额修改，core/noTellerTransSignDef" }
//--信用卡
,{code : "WDD/C29", desc : "信用卡当前积分查询，credit/creditCardPointQuery.xml" }
,{code : "WDD/C30", desc : "信用卡已出账单头查询，credit/creditCardBillHeadQuery" }
,{code : "WDD/C31", desc : "信用卡额度查询，credit/creditCardLimitQuery" }
,{code : "WDD/C32", desc : "信用卡账户详情查询，credit/creditCardInfoQuery" }
,{code : "WDD/C33", desc : "信用卡预借现金，credit/cashAdvance" }
,{code : "WDD/C34", desc : "信用卡现金分期申请，credit/cashPerApply" }
,{code : "WDD/C35", desc : "信用卡现金分期参数查询，credit/cashPerParameterQuery" }
,{code : "WDD/C36", desc : "信用卡激活，credit/creditCardActivate" }
,{code : "WDD/C37", desc : "信用卡客户资料查询（地址类)，credit/creditCardAddrModifyQuery" }
,{code : "WDD/C38", desc : "信用卡客户资料更新(地址类)，credit/creditCardAddrModifyUpdate" }
,{code : "WDD/C39", desc : "信用卡进度查询，credit/creditCardApplyQuery" }
,{code : "WDD/C40", desc : "已出账单分期申请，credit/creditCardBillApply" }
,{code : "WDD/C41", desc : "信用卡已出账单列表查询，credit/creditCardBillPageQuery" }
,{code : "WDD/C42", desc : "已出账单分期试算，credit/creditCardBillTrial" }
,{code : "WDD/C43", desc : "信用卡客户资料查询（手机，EMAIL），credit/creditCardContactWayModifyQuery" }
,{code : "WDD/C44", desc : "信用卡客户资料更新（手机，EMAIL），credit/creditCardContactWayModifyUpdate" }
,{code : "WDD/C45", desc : "卡片补卡，credit/creditCardFill" }
,{code : "WDD/C46", desc : "固定额度调低，credit/creditCardFixedLimitMag" }
,{code : "WDD/C47", desc : "已出账单分期参数查询，credit/creditCardHaveCheckParamQuery" }
,{code : "WDD/C48", desc : "信用卡历史积分明细查询，credit/creditCardHisPointPageQuery" }
,{code : "WDD/C49", desc : "信用卡激活状态查询，credit/creditCardInfoQuery" }
,{code : "WDD/C50", desc : "卡片挂失，credit/creditCardLost" }
,{code : "WDD/C51", desc : "信用卡补寄账单，credit/creditCardMailCheck" }
,{code : "WDD/C52", desc : "未出账单分期参数查询，credit/creditCardNoCheckParamQuery" }
,{code : "WDD/C53", desc : "信用卡交易密码设置，credit/creditCardPassWordMsg" }
,{code : "WDD/C54", desc : "信用卡交易密码状态查询，credit/creditCardPassWordQuery" }
,{code : "WDD/C55", desc : "信用卡查询密码设置，credit/creditCardQueryPwdMsg" }
,{code : "WDD/C56", desc : "信用卡还款本人，credit/creditCardRepayMentOthers" }
,{code : "WDD/C57", desc : "信用卡自动还款账户修改，credit/creditCardRepayMentAmend" }
,{code : "WDD/C58", desc : "信用卡自动还款账户查询，credit/creditCardRepayMentQuery" }
,{code : "WDD/C59", desc : "信用卡自动还款账户撤销，credit/creditCardRepayMentRepeal" }
,{code : "WDD/C60", desc : "信用卡还款他人，credit/creditCardRepayMentSelf" }
,{code : "WDD/C61", desc : "信用卡自动还款账户设置，credit/creditCardRepayMentSet" }
,{code : "WDD/C62", desc : "信用卡未出账单明细查询，credit/creditCardUnsettledBillPageQuery" }
,{code : "WDD/C63", desc : "分期历史查询，credit/installMentsPageQry" }
,{code : "WDD/C64", desc : "可未出账单分期查询，credit/mayNoCheckPerQuery" }
,{code : "WDD/C65", desc : "未出账单分期申请，credit/noCheckPerApply" }
,{code : "WDD/C66", desc : "未出账单分期试算，credit/noCheckPerTrial" }
,{code : "WDD/C67", desc : "信用卡现金分期试算，credit/cashPerTrial" }

//--D组交易场景
,{code : "SC/D00", desc : "YYM场景描述，登录前" }
,{code : "SC/D01", desc : "贷款合同查询，用户名下有贷款合同且贷款合同未结清" }
,{code : "SC/D02", desc : "贷款合同查询，用户名下有贷款合同且贷款合同已结清" }
,{code : "SC/D03", desc : "贷款合同查询，用户名下无贷款合同" }
,{code : "SC/D04", desc : "贷款合同查询，用户无权限查询贷款合同" }
,{code : "SC/D06", desc : "贷款在线自助放款，用户没有放款权限，提示用户进行升级" }
,{code : "SC/D05", desc : "贷款在线自助放款，贷款合同没有可用余额" }
,{code : "SC/D08", desc : "贷款在线自助放款，用户放款账号异常" }
,{code : "SC/D09", desc : "贷款在线自助放款，贷款合同有可用余额且只放一笔借款" }
,{code : "SC/D10", desc : "贷款在线自助放款，贷款合同有可用余额且放多笔笔借款" }
,{code : "SC/D11", desc : "贷款借据查询，查询某一合同下的借据且用户无查询借据的权限" }
,{code : "SC/D11", desc : "贷款借据查询，查询某一合同下的借据且合同下无借据" }
,{code : "SC/D11", desc : "贷款借据查询，查询某一合同下的借据且合同下有未结清借据" }
,{code : "SC/D11", desc : "贷款借据查询，查询某一合同下的借据且合同下有已结清借据" }
,{code : "SC/D12", desc : "贷款借据查询，查询用户名下的借据且用户无查询借据的权限" }
,{code : "SC/D13", desc : "贷款借据查询，查询用户名下的借据且用户名下无借据" }
,{code : "SC/D14", desc : "贷款借据查询，查询用户名下的借据且用户名有未结清借据" }
,{code : "SC/D15", desc : "贷款借据查询，查询用户名下的借据且用户名有已结清借据" }
,{code : "SC/D16", desc : "贷款在线自助还款，部分还款" }
,{code : "SC/D17", desc : "贷款在线自助还款，全额还款" }
,{code : "SC/D17", desc : "贷款在线自助还款，用户还款账号异常" }
,{code : "SC/D18", desc : "贷款借据欠款查询，用户借据未结清" }
,{code : "SC/D19", desc : "贷款借据历史还款明细查询，借据无还款记录" }
,{code : "SC/D19", desc : "贷款借据历史还款明细查询，借据有还款记录且分页" }
,{code : "SC/D20", desc : "贷款借据还款计划查询，未结清借据查询还款计划且还款计划记录需分页" }
,{code : "SC/D21", desc : "贷款借据还款账号变更，还款账号变更为二类电子账户" }
,{code : "SC/D22", desc : "贷款借据还款账号变更，还款账号变更为三类电子账户" }
,{code : "SC/D23", desc : "贷款借据还款账号变更，还款账号变更为用户已下挂的借记卡且借记卡状态正常" }
,{code : "SC/D24", desc : "贷款借据还款账号变更，还款账号变更为用户已下挂的借记卡且借记卡状态异常" }

,{code : "SC/D25", desc : "消息中心消息类型信息列表查询，用户消息类型展示" }
,{code : "SC/D26", desc : "消息中心查询用户APP消息，所有用户App消息" }
,{code : "SC/D27", desc : "消息中心最新消息列表，最新的用户App消息" }
,{code : "SC/D28", desc : "消息中心APP消息记录更新，阅读后的App消息更新" }
,{code : "SC/D29", desc : "消息中心APP消息记录删除，删除用户某个App消息" }
,{code : "SC/D30", desc : "消息中心APP消息记录一键阅读，阅读所有某个类型的App消息" }
,{code : "SC/D31", desc : "查询勋章墙，用户勋章等级的查询" }
,{code : "SC/D32", desc : "查询升级任务列表，用户勋章等级详情" }

,{code : "HFY/D041301",desc : "好友圈用户昵称查询,如果有昵称进入最近消息列表,如果没有昵称提示用户设置昵称"}
,{code : "HFY/D041302",desc : "好友圈我的昵称设置,用户设置昵称"}
,{code : "HFY/D041303",desc : "好友圈最近消息列表查询,查询最近消息列表"}
,{code : "HFY/D041304",desc : "好友圈用户消息读取状态更新,用户与好友之间已发送消息读取状态，如果未读取显示未读标志"}
,{code : "HFY/D041305",desc : "好友圈用户消息读取状态更新,用户与好友之间已发送消息读取状态，如果已经读取不显示未读标志"}
,{code : "HFY/D041306",desc : "好友圈用户消息删除,最近消息列表侧滑删除"}
,{code : "HFY/D041307",desc : "好友圈用户消息列表查询,用户与某个好友详细消息列表"}
,{code : "HFY/D041308",desc : "好友圈AA付款消息新增,付款完成后调用付款消息新增"}
,{code : "HFY/D041309",desc : "好友圈AA收款消息新增,收款完成后调用收款消息新增"}
,{code : "HFY/D041310",desc : "好友圈转账消息新增,转账完成后调用转账消息新增"}
,{code : "HFY/D041311",desc : "好友圈个人名片消息新增,个人名片发送成功调用个人名片消息新增"}
,{code : "HFY/D041312",desc : "好友圈分享消息新增,分享完成后调用分享消息新增"}
,{code : "HFY/D041313",desc : "好友圈发送红包-消息新增,红包发送成功后调用红包消息新增"}
,{code : "HFY/D041314",desc : "好友圈领取红包-消息新增,红包领取成功后调用红包消息新增"}
,{code : "HFY/D041315",desc : "好友圈退回红包-消息新增,红包退回成功后调用红包消息新增"}
,{code : "HFY/D041316",desc : "好友圈好友列表查询,通讯录好友列表查询"}
,{code : "HFY/D041317",desc : "好友圈分组列表查询,通讯录分组查询"}
,{code : "HFY/D041318",desc : "好友圈分组添加,用户新建分组"}
,{code : "HFY/D041319",desc : "好友圈分组删除,用户删除分组"}
,{code : "HFY/D041320",desc : "好友圈分组成员查询,点击某个分组查询当前分组的成员"}
,{code : "HFY/D041321",desc : "好友圈分组更新,用户在某个分组添加或删除好友后调用分组更新的接口"}
,{code : "HFY/D041322",desc : "好友圈用户好友详情查询,通讯录好友详细信息，昵称,电话号码,这个页面可以转账,发送红包如果不是好友显示添加好友的按钮"}
,{code : "HFY/D041323",desc : "好友圈备注信息查询,查询好友备注信息"}
,{code : "HFY/D041324",desc : "好友圈备注信息设置,通讯录首页侧滑进入备注设置"}
,{code : "HFY/D041325",desc : "好友圈好友添加,添加好友"}
,{code : "HFY/D041326",desc : "好友圈好友申请,添加好友发送验证信息"}
,{code : "HFY/D041327",desc : "好友圈好友请求记录查询,查询好友添加申请记录"}
,{code : "HFY/D041328",desc : "好友圈好友请求记录删除,侧滑删除申请记录"}
,{code : "HFY/D041329",desc : "好友圈好友请求记录查询,查询好友申请记录"}
,{code : "HFY/D041330",desc : "好友圈好友删除"}

,{code : "JYF/D1101",desc : "预约填单新增,core/preConFormAdd"}
,{code : "JYF/D1102",desc : "预约填单删除-废弃,core/preConFormDel"}
,{code : "JYF/D1103",desc : "预约填单信息查询,core/preConFormQuery"}
,{code : "JYF/D1104",desc : "预约填单修改,core/preConFormUpd"}


,{code : "PXL/E01", desc : "O2O生活圈，快捷支付O2O" }
,{code : "PXL/E02", desc : "O2O生活圈，他行卡支付O2O" }
,{code : "PXL/E03", desc : "O2O生活圈，余额支付O2O" }
,{code : "PXL/E04", desc : "O2O生活圈，O2O订单创建(生活圈)" }
,{code : "PXL/E05", desc : "O2O生活圈，O2O订单退款(生活圈)" }
,{code : "PXL/E06", desc : "O2O生活圈，O2O订单状态查询(生活圈)" }
,{code : "PXL/E07", desc : "O2O生活圈，O2O订单状态查询(生活圈)" }
,{code : "PXL/E08", desc : "O2O生活圈，商品核销" }
,{code : "PXL/E09", desc : "O2O生活圈，电子对账单" }
,{code : "PXL/E10", desc : "O2O生活圈，手工清分" }
,{code : "PXL/E11", desc : "O2O生活圈，手工清分查询" }


];