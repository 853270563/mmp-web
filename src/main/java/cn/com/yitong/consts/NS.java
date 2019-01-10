package cn.com.yitong.consts;

/**
 * NameSpace公共的字段名称规范
 * 
 * @author yaoym
 * 
 */
public class NS {

	// 数据字典相关
	public static final String DICT_SYS_PARAMS_SESS_TIMEOUT = "SESSION_TIMEOUT_DEFUALT";	// 系统配置之会话超时配置
	public static final String DICT_SYS_PARAMS_DEVICE_CHECK = "DEVICE_CHECK";	// 系统配置之设备检查配置
	public static final String DICT_SYS_PARAMS_DEVICE_ROOT_CHECK = "DEVICE_ROOT_CHECK";	// 系统配置之设备root检查配置
	public static final String LOGINACCOUNTLIMTTYPE = "userSessCtlType"; //账号级登录控制类型【0：不控制；1：单一设备；2：单一会话】
    public static final String LOGINLIMTCNT = "TotalSessCount";  //总体登陆限制数
	public static final String FORCEKOGINTYPE = "FORCE_LOGIN_STATUS";  //服务端是否支持强制登陆

	// 会话相关----------begin---------------
	public static final String APP = "APP";// 前端应用标识
	public static final String SEV_IP = "SEV_IP";// 登录IP
	public static final String CIF_NO = "CIF_NO";// 客户号
	public static final String UAS_USER_ID = "UAS_USER_ID";// UAS用户ID
	public static final String TFB_CIF_NO = "TFB_CIF_NO";// 網銀唯一标识
	public static final String IBS_LGN_ID = "IBS_LGN_ID";// 登录号
	public static final String AUTH_TYP = "AUTH_TYP";// 认证方式
	public static final String LANGUAGE = "LANGUAGE"; // 语言
	public static final String SESS_SEQ = "SESS_SEQ";// 应用内部会话编号
	public static final String USER_SESS_ID = "JSESS_ID";// HttpSessionId会话编号
	public static final String TRANS_CODE = "TRANS_CODE";// 交易编号
	public static final String TRANS_LOG_SEQ = "TRANS_LOG_SEQ";// 交易日志序列
	public static final String RESE_MESS = "RESE_MESS"; // 用戶預留信息
	public static final String SUCC_LGN_CNT = "SUCC_LGN_CNT"; // 登录成功次数
	public static final String LGN_LAST_DATE = "LGN_LAST_DATE"; // 上次登录日期
	public static final String LGN_LAST_TIME = "LGN_LAST_TIME"; // 上次登录时间
	public static final String NEW_OLD_STAT = "NEW_OLD_STAT";// 新老网银标识

	public static final String CLI_IP = "CLI_IP";// 客户端IP
	public static final String CLI_CHANEL = "CHANEL";// 客户端渠道

	public static final String LOSE_DATE = "LOSE_DATE"; // 会话超时日期
	public static final String LOSE_TIME = "LOSE_TIME";// 会话超时时间

	public static final String AUTH_TYP_NULL = "0";// 认证方式
	public static final String AUTH_TYP_SMS = "1";// 认证方式
	public static final String AUTH_TYP_TOKEN = "2";// 认证方式

	public static final String ACCT_TYP_CA = "CA";// 1往来
	public static final String ACCT_TYP_SA = "SA";// 2储蓄
	public static final String ACCT_TYP_FS = "FS";// 3多币宝
	public static final String ACCT_TYP_FD = "FD";// 4定期
	public static final String ACCT_TYP_MI = "MI";// 5理财
	public static final String ACCT_TYP_LN = "LN";// 6贷款
	public static final String ACCT_TYP_CR = "CR";// 7信用卡账户
	public static final String ACCT_TYP_ST = "ST";// 8股票账户

	public static final String LOG_OTH_MSG = "OTH_COMM";// 日志補充消息

	// 会话相关----------end---------------

	// 附属賬號相关------begin---------------
	public static final String ACCT_NO = "ACCT_NO";// 賬號
	public static final String ACCT_TYP = "ACCT_TYP";// 賬號类型
	public static final String CCY_NO = "CCY_NO";// 币别
	public static final String ACCT_ALIAS = "ACCT_ALIAS";// 賬號别名
	public static final String ACCT_STAT = "ACCT_STAT";// 賬號状态
	public static final String ACCT_STATUS = "ACCT_STATUS";// 賬號状态
	public static final String FRZ_STATUS = "FRZ_STATUS";// 賬號状态
	public static final String ACCT_STATUS_DESC = "ACCT_STATUS_DESC";// 賬號类型
	public static final String ACCT_STATUS_DESC2 = "ACCT_STATUS_DESC2";// 賬號类型
	public static final String ORD = "ORD";// 賬號排序
	// 附属賬號相关------end---------------

	// 转账相关----------begin---------------
	public static final String PAY_ACCT_NO = "PAY_ACCT_NO";// 支款賬號
	public static final String PAY_ACCT_TYP = "PAY_ACCT_TYP";// 支款賬號类型
	public static final String PAY_CURR = "PAY_CURR";// 支款币别
	public static final String PAY_AMT = "PAY_AMT";// 支款金额
	public static final String PAY_ACCT_ALIAS = "PAY_ACCT_ALIAS";// 支款賬號别名
	public static final String PAY_ACCT_STAT = "PAY_ACCT_STAT";// 支款賬號状态

	public static final String RECV_ACCT_NO = "RECV_ACCT_NO";// 收款賬號
	public static final String RECV_ACCT_TYP = "RECV_ACCT_TYP";// 收款賬號类型
	public static final String RECV_CURR = "RECV_CURR";// 收款币别
	public static final String RECV_AMT = "RECV_AMT";// 收款金额
	public static final String RECV_ACCT_ALIAS = "RECV_ACCT_ALIAS";// 收款賬號别名
	public static final String RECV_ACCT_STAT = "RECV_ACCT_STAT";// 收款賬號状态
	// 转账相关----------end---------------

	public static final String LIST = "LIST";
	public static final String DATA = "DATA";
	public static final String IBATIS_STATEMENT = "ibatisStatement";

	public static final String ACCT_NUM = "ACCT_NUM";
	public static final String HAS_NEXT_PAGE = "HAS_NEXT_PAGE";
	public static final String NEXT_PAGE = "NEXT_PAGE";
	public static final String NEXT_KEY = "NEXT_KEY";
	public static final String PAGE_NO = "PAGE_NO";
	public static final String PAGE_SIZE = "PAGE_SIZE";
	public static final String STR_DATE = "STR_DATE";
	public static final String END_DATE = "END_DATE";
	
	

	public static final String START_ROW = "startRow";
	public static final String END_ROW = "endRow";
	// 一次性验证码
	public static String RANDOM_CODE = "RANDOM_CODE";

	public static String NOTICE_ID = "NoticeId";
	public static String RECORD_ID = "RECORD_ID";

	// 日志相关----------begin---------------
	public static String TRANS_FLG = "TRANS_FLG";// 0登录;1动账;2非动账;
	public static String TRANS_FLG_ANSY = "TRANS_FLG_ANSY";// 动账交易号
	public static String TRANS_FLG_LOGIN = "TRANS_FLG_LOGIN";// 动账交易号

	public static String TRANS_FLG_0 = "0";// 0登录;1动账;2非动账;
	public static String TRANS_FLG_1 = "1";// 0登录;1动账;2非动账;
	public static String TRANS_FLG_2 = "2";// 0登录;1动账;2非动账;

	// 日志相关----------end---------------
	// 一次性会话参数
	public static final String SIGNLE_SEQ = "SIGNLE_SEQ";// aastocks密码
	public static final String STREAM_FLAG = "STREAM_FLAG";// 串流开通标识位
	public static final String STEAM_SRV_NO = "STEAM_SRV_NO"; // aastocks賬號

	// 股票服务串流相关
	public static final String AASTOCKS_PWD = "AASTOCKS_PWD";//
	public static final String DEFAULT_IMG = "css/images/face/lotus.png";// 默认头像
	public static final String QUIC_MENU_FLG = "QUIC_MENU_FLG";
	
	
	//设备擦除-绑定相关---------begin---------
	public static final String DEVICE_LOCK_STATUS_YES = "1";  //设备锁定
	public static final String DEVICE_LOCK_STATUS_NO = "0";   //设备未锁定
	public static final String DEVICE_ERASE_STATUS_YES = "1"; //设备需要擦除数据
	public static final String DEVICE_ERASE_STATUS_NO = "0";  //设备无需擦除数据
	public static final String DEVICE_LOSE_STATUS_YES = "1"; //设备遗失状态-遗失
	public static final String DEVICE_LOSE_STATUS_NO = "0";  //设备遗失状态-正常
	
	public static final String DEVICE_ERASE_RESULT_SUCCESS = "1"; //设备擦除结果-成功
	public static final String DEVICE_ERASE_RESULT_FAIL = "0";    //设备擦除结果-失败
	
	public static final String DEVICE_UNDO_BOUND_RESULT_FAIL = "0";    //设备解绑结果-失败
	public static final String DEVICE_UNDO_BOUND_RESULT_SUCCESS = "1";    //设备解绑结果-成功
	
	public static final String DEVICE_BOUND_RESULT_FAIL = "0";    //设备注册结果-失败
	public static final String DEVICE_BOUND_RESULT_SUCCESS = "1";    //设备注册结果-成功

	public static final String DEVICE_IS_ROOT_YES = "1";    //设备root--root
	public static final String DEVICE_IS_ROOT_NO= "0";    //设备root--未root
	
	public static final String DEVICE_CHECK_RESULT_FAIL = "0";    //登陆时-设备校验结果--失败
	public static final String DEVICE_CHECK_RESULT_REGISTER = "1";    //登陆时-设备校验结果--需要注册
	public static final String DEVICE_CHECK_RESULT_SUCCESS = "2";    //登陆时-设备校验结果--成功

	public static String DEVICE_APP_TYPE_ALL = "2";			//设备应用分类--所有
	public static String DEVICE_APP_TYPE_BLACK = "0";		//设备应用分类--黑名单
	public static String DEVICE_APP_TYPE_WHITE = "1";		//设备应用分类--白名单
	//设备擦除-绑定相关---------end-----------
	
	
	//数据库监听器---------begin---------
	public static final String RATE_INTE = "rate_inte";		//数据库监听器-利率   
	public static final String EXCHANGE_RATE = "exchange_rate";		//数据库监听器-汇率 
	public static final String CHARGE = "charge";		//数据库监听器-资费
	//数据库监听器---------end-----------

	// 应用管理----begin---------------------
	public static final String APP_STATUS_UP = "1";	//应用管理--应用状态-上架
	public static final String APP_STATUS_DOWN = "0";	//应用管理--应用状态-下架
	// 应用管理----end-----------------------

	// 统一门户----begin---------------------
	public static final String PORTAL_AUTH_CODE_INVALID = "1"; //统一门户--授权码状态-无效
	public static final String PORTAL_AUTH_CODE_VALID = "0"; //统一门户--授权码状态-有效
	// 统一门户----end-----------------------
	
	
	// -XXXX----begin---------------------
	// -XXXX----end-----------------------

}
