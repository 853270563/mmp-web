package cn.com.yitong.consts;

/**
 * NameSpace公共的字段名称规范
 * 
 * @author yaoym
 * 
 */
public class NS {

	// 数据字典 设备会话控制 -----begin----------
	public static final String DICT_SYS_PARAMS_SESS_TIMEOUT = "SESSION_TIMEOUT_DEFUALT";	// 系统配置之会话超时配置
	public static final String DICT_SYS_PARAMS_DEVICE_CHECK = "DEVICE_CHECK";	// 系统配置之设备检查配置
	public static final String DICT_SYS_PARAMS_MAX_USER_BOUND_DEVICE_NUM = "MAX_USER_BOUND_DEVICE_NUM";	// 系统配置之用户最大绑定设备数配置
	public static final String DICT_SYS_PARAMS_DEVICE_ROOT_CHECK = "DEVICE_ROOT_CHECK";	// 系统配置之设备root检查配置
	public static final String LOGINACCOUNTLIMTTYPE = "userSessCtlType"; //账号级登录控制类型【0：不控制；1：单一设备；2：单一会话】
    public static final String LOGINLIMTCNT = "TotalSessCount";  //总体登陆限制数
	public static final String FORCEKOGINTYPE = "FORCE_LOGIN_STATUS";  //服务端是否支持强制登陆
	// 数据字典 设备会话控制 -----end----------

	// 数据字典  用户类型 ------begin------------
	public static final String DICT_USER_TYP_ALL = "0"; // 移动和后台用户
	public static final String DICT_USER_TYP_MOBILE = "1"; // 移动用户
	public static final String DICT_USER_TYP_WEB = "2"; // 后台用户
	//数据字典  用户类型 ------end------------

	// 登陆用户等信息 ----begin-------------
	public static final String LOGIN_ID = "LGN_ID";	// 网银登录号
	public static final String ORGAN_ID = "ORG_ID"; // 用户所在机构ID
	public static final String USER_NO = "USER_NO";	// 用户员工号
	public static final String LANGUAGE = "LANGUAGE";// 语言
	public static final String AUTH_TYPE = "AUTH_TYP";	//授权方式
	public static final String IMG_CODE = "validateCode";	// 图形验证码
	public static final String ISLOGIN = "isLogin";	// 登陆状态
	public static final String SIGNLE_SEQ = "SIGNLE_SEQ";	// 一次性会话
	// 登陆用户等信息 ----end-------------

	// 日志相关----------begin---------------
	public static final String TRANS_CODE = "TRANS_CODE";// 交易编号
	public static final String TRANS_LOG_SEQ = "TRANS_LOG_SEQ";// 交易日志序列
	public static final String TRANS_SESSION_ID = "SESSION_ID";// 交易会话Id
	public static final String TRANS_COST = "TRANS_COST";// 交易耗时
	public static final String DEVICE_ID = "DEVICE_ID";// 交易设备Id
	public static final String TRAN_DATE = "TRAN_DATE"; //交易日期
	public static final String TRAN_TIME = "TRAN_TIME"; //交易时间
	// 日志相关----------end---------------

	public static final String LIST = "LIST";
	public static final String DATA = "DATA";
	public static final String IBATIS_STATEMENT = "ibatisStatement";

	// 分页静态项配置 ---------------start-----------
	public static final String NEXT_PAGE = "NEXT_PAGE";
	public static final String NEXT_KEY = "NEXT_KEY";
	public static final String PAGE_NO = "PAGE_NO";
	public static final String PAGE_SIZE = "PAGE_SIZE";
	public static final String START_ROW = "startRow";
	public static final String END_ROW = "endRow";
	// 分页静态项配置 ---------------end-----------

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

	public static final String DEVICE_APP_TYPE_ALL = "2";			//设备应用分类--所有
	public static final String DEVICE_APP_TYPE_BLACK = "0";		//设备应用分类--黑名单
	public static final String DEVICE_APP_TYPE_WHITE = "1";		//设备应用分类--白名单
	//设备擦除-绑定相关---------end-----------

	// -XXXX----begin---------------------
	public static final String SESSION_TOKEN_IS_REUSE_YES = "1"; //无会话是否保持上一个token --保持
	public static final String SESSION_TOKEN_IS_REUSE_NO = "0"; //无会话是否保持上一个token --不保持
	// -XXXX----end-----------------------
}
