package cn.com.yitong.consts;

/**
 * 会话常量
 * 
 * @author yaoym
 * 
 */
public interface SessConsts {
	// 14位長度交易會話
	public static final String SESS_SEQ = "SESS_SEQ";
	public static final String SSID = "SSID";
	
	// session_id
	public static final String SESSION_ID = "SESSION_ID";
	
	public static final String FIRST_SESSION="FIRST_SESSION";
	//上次退出狀態 0未设置；1正常退出；2会话过期退出；3会话被踢出；';
	public static final String EXIT_STAT = "EXIT_STAT";
	//強制修改密碼標誌  0：不修改  1：修改
	public static final String REST_PWD_FLAG = "REST_PWD_FLAG";
	// 客户号
	public static final String CIF_NO = "CIF_NO";
	// 网银登录号
	public static final String LOGIN_ID = "LGN_ID";
	
	// 用户所在机构ID
	public static final String ORGAN_ID = "ORG_ID";
	// 用户角色
	public static final String ROLE_ID = "ROLE_ID";
	//用户信息
	public static final String USER_INFO = "USER_INFO";
	// session日志ID
	public static final String SESS_ID = "SESS_ID";
	// 语言
	public static final String LANGUAGE = "LANGUAGE";
	// CUST_SEX CUST_IDT CUST_IDT_NO CUST_INFO LOGN_TIME CUST_NAME
	// 性别
	public static final String CUST_SEX = "CustSex";
	// 证件类型
	public static final String CUST_IDT = "CustIdt";
	public static final String CUST_INFO = "CustInfo";
	// 客户名称
	public static final String CUST_NAME = "CustName";
	public static final String AUTH_TYPE = "AUTH_TYP";
	
	// BTT后端服务会话ID
	public static final String BTT_SET_COOKIE = "Set-Cookie";
	public static final String BTT_COOKIE = "Cookie";
	

	// 图形验证码
	public static final String IMG_CODE = "validateCode";
	// 激活码
	public static final String RANDOM_CODE = "randomCode";
	// 激活码有效时间
	public static final String RANDOM_CODE_TIME = "randomCodeTime";
	// 账户列表
	public static final String ACCT_MAP = "AcctMap";
	// 手机号
	public static final String MOBILE_NO = "MobileNo";
	//区号
	public static final String AREA = "AREA";
	// 登陆状态
	public static final String ISLOGIN = "isLogin";
	// 客户端版本
	public static final String CLIENT_VERSION = "ClientVersion";
	// 客户端IP
	public static final String CLIENT_IP = "ClientIp";
	// 客户端唯一标识
	public static final String CLIENT_IDT = "ClientIdt";
	// 客户端信息
	public static final String CLIENT_DESCRIPT = "ClientDescript";
	// 客户端系统类型
	public static final String CLIENT_PLATFORM = "OS_TYPE";
	// 上次功能访问记录序列
	public static final String FUN_VIST_LOG_SEQ = "FUN_VIST_LOG_SEQ";
	
	//是為首次登錄 0:否 1:是
	public static final Object LGN_FIRST = "LGN_FIRST";
	
	
	// -n 天前日期----begin---------------------
	public static final String BEFORE_TWO_DAYS = "BEFORE_TWO_DAYS";
	public static final String BEFORE_SEVEN_DAYS = "BEFORE_SEVEN_DAYS";
	public static final String BEFORE_NINETY_DAYS = "BEFORE_NINETY_DAYS";
	// -n 天前日期----end---------------------
	// 一次性会话
	public static final String SIGNLE_SEQ = "SIGNLE_SEQ";//

	public static final String TIME_DOWNLOAD = "TIME_DOWN_LOAD";
	
}
