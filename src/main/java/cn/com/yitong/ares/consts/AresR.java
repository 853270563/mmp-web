package cn.com.yitong.ares.consts;

import java.util.ArrayList;
import java.util.List;

public interface AresR {

    public static final Object[] EMPTY_PARAMS = new Object[] {};
    public static final List EMPTY_LIST = new ArrayList();
	
    // ==============================
    public static final String TRANS_CODE = "TRANS_CODE";
    public static final String TRANS_URL = "TRANS_URL";
    public static final String RESULT_OK = "ok";//表示通讯成功
    public static final String RESULT_00 = "00";//表示数据成功
    public static final String RESULT_DATA = "data";
    public static final String RESULT_ERROR_MSG = "msg";
    public static final String RESULT_ERROR_CODE = "errorCode";
    public static final String RESULT = "result";
    public static final String SESSION_ID = "SESSION_ID";//会话ID
    public static final String SESSION_TOKEN = "SESSION_TOKEN";//会话TOKEN
    public static final String IMAGE_CODE = "IMAGE_CODE";//图形码
    public static final String CHECK_IMAGE_CODE = "CHECK_IMAGE_CODE";// 校验图形码标识，值大于0时必须校验
    public static final String LOGIN_FLAG = "LOGIN_FLAG";// true为已登录；其它为未登录
    public static final String LOGIN_ID = "LOGIN_ID"; //登录号
    public static final String LGN_PWD = "LGN_PWD";//登录密码
    public static final String LGN_USER_IP = "LGN_USER_IP";//登录终端IP
    public static final String CALL_METHOD = "CALL_METHOD";//登录接口调用方法名
    public static final String ACCTS = "ACCTS";//附属账号集
    public static final String CLIENT_NEED_SIGN = "CLIENT_SIGN"; //需设备绑定标记
    public static final String LOGIN_PWD_NEED_RESET = "LOGIN_PWD_RESET"; //需强制修改登录密码
    public static final String LOGIN_TOKEN = "LOGIN_TOKEN";//登录TOKEN

    public static final String CLIENT_IP = "@CLIENT_IP";//	客户端IP地址
    public static final String CLIENT_VER_NO = "CLIENT_VER_NO";//	客户端版本
    public static final String CLIENT_TYPE = "CLIENT_TYPE";//	客户端类型： IP: IPHONE PD: IPAD  AD: ANDROID  AP: ANDROID PAD
    public static final String CLIENT_OS = "CLIENT_OS";//	客户端操作系统 I:IOS  A:ANDROID
    public static final String CLIENT_INFO = "CLIENT_INFO";//	客户端详细信息  如魅族  小米等信息
    public static final String CLIENT_NO = "CLIENT_NO";//	设备号
    public static final String X_LINE = "X_LINE";//	经度
    public static final String Y_LINE = "Y_LINE";//	纬度

    // 用户基本信息
    public static final String SID = "SID";// 办公系统SID
    public static final String CUST_NAME = "CUST_NAME";//客户名称
    public static final String EMAIL = "EMAIL";//客户名称
    public static final String OFIC_ADDR = "OFIC_ADDR";//客户名称
    public static final String DEPT_ID = "DEPT_ID";//客户名称
    public static final String OPER_ID = "OPER_ID";//客户名称
    public static final String POIT_ID = "POIT_ID";//客户名称
    public static final String POIT_NAME = "POIT_NAME";//客户名称
    public static final String OUT_TEL = "OUT_TEL";//客户名称
    public static final String INNER_TEL = "INNER_TEL";//客户名称
    public static final String SEX = "SEX";// 性别
    public static final String MOBILE = "MOBILE";//签约手机号
    
    public static final String CIFN = "CIFN";// 核心用户号
    public static final String CUST_NO = "CUST_NO";// 电子渠道内部客户号
    public static final String MOBILE_NO_PAY = "MOBILE_NO_PAY";//支付手机号
    public static final String IS_BIND_MOBILE_AGAIN = "IS_BIND_MOBILE_AGAIN";//支付手机号
    public static final String RES_INFO = "RES_INFO";// 预留信息
    public static final String IDT_NO = "CUST_IDT_NO";// 用户证件号
    public static final String IDT_TYPE = "CUST_IDT";// 用户证件类型
    public static final String LOGIN_SUCC_NUM = "LOGIN_SUCC_NUM";// 登录次数
    public static final String LAST_LGN_TIME = "LAST_LGN_TIME";//上次登陆时间
    public static final String LAST_LGN_ADDR = "LAST_LGN_ADDR";//上次登录地址
    public static final String CUST_LGN_TYP = "CUST_LGN_TYP";//客户登陆类型
    public static final String CUST_LGN = "CUST_LGN";//客户登陆账号
    public static final String CUST_EMAIL = "CUST_EMAIL";//邮箱
    public static final String CUST_ISEMP = "CUST_ISEMP";//员工标识 Y: 是员工;N: 非员工
    public static final String TRUE = "true";// true为已登录；其它为未登录
    
    
    
    /* *************** 登录方式 *************** */
    public static final String IS_SUP_FINGER = "IS_SUP_FINGER";//是否支持指纹登录
    public static final String FINGER = "FINGER";//是否指纹登录
    public static final String LOGIN_TYPE = "LOGIN_TYPE";//登录方式
    public static final String LOGIN_BY_IDT = "0";//证件号
    public static final String LOGIN_BY_UID = "1";//用户号
    public static final String LOGIN_BY_GESTURE = "2";//手势密码
    public static final String LOGIN_BY_FP = "3";//指纹
    
    public static final String CUST_STATUS_OK = "N";//客户正常状态
    public static final String LGN_ERR_NUM = "LGN_ERR_NUM";//登录密码输入错误次数
    public static final String LGN_ERR_LMT = "LGN_ERR_LMT";//密码输入错误次数上限
    public static final String PWD_PRE_FLAG = "PWD_PRE_FLAG";//密码初始状态
    public static final String CARD_LIST = "CARD_LIST";//卡列表
    public static final boolean ENC_SM = true;//是否国密


    // TOKEN
    public static final String TOKEN_CODE = "TOKEN";//单次会话检查，随机会话字符串
    public static final String TOKEN_TIME = "TOKEN_TIME";// TOKEN生成时间
    public static final String CHECK_TOKEN = "CHECK_TOKEN";//是否校验TOKEN Y-是 N-否
    public static final int TOKEN_TIME_LIMIT = 5000;// 10s内不允许生成新的验证码
    public static final int TOKEN_TIME_OUT = 120000;// TOKEN有效事假

    // 动态验证码
    public static final String DYN_CODE = "DYN_CODE";//动态验证码检查，随机会话字符串
    public static final String DYN_TIME = "DYN_TIME";// 动态验证码生成时间
    public static final int DYN_TIME_OUT = 180000;// 动态验证码有效时间
    public static final int DYN_REPEAT_TIME = 60000;// 动态验证码有效时间

    // 报文请求防重放 & 重复提交
    public static final String REQ_TIME = "REQ_TIME";    // 报文请求时间戳（时间戳格式：YYYYMMDDHHmmss）
    public static final int REQ_REPLAY_TIME = 3;    // 报文请求防重放验证码容错时间（单位：秒）
    public static final int REQ_REPEAT_TIME = 5;    // 报文请求重复提交时间,此时间间隔内相同交易视为重复提交（单位：秒）
    
    //交易状态
    public static final String RTN_SUCCESS = "1";
    public static final String RTN_MSG = "MSG";
    public static final String RTN_CODE = "STATUS";

    /* *************** 加解密常量 *************** */
    // 自定义加密版本
    public static final String VERSION = "1";
    // 自定义加密填充约束
    public static final String FILL_CODE = "13";
    // 混淆规则 0：无混淆；1：首尾对换；2、奇偶对换；
    public static final Integer CONFUSE_STATUS = 2;
    // 请求加密KEY
    public static final String DYNAMIC_KEY = "DYNAMIC_KEY";

    //通讯配置文件目录
    public static String HTTP_PRE_PATH = "inte/";
    public static String SOCKET_PRE_PATH = "inte/";
    public static String WEB_PRE_PATH = "web/";// 此目录下的xml用于对返回前端数据进行过滤
    public static String SOAP_PRE_PATH = "inte/";

    //分页参数设置
    public static final String NEXT_KEY_DEFAULT = "1";
    public static final String PAGE_SIZE_DEFAULT = "5";
    public static final String NEXT_KEY = "NEXT_KEY";
    public static final String PAGE_SIZE = "PAGE_SIZE";
    public static final String START_ROW = "START_ROW";
    public static final String END_ROW = "END_ROW";

    /* *************** 以下为通讯报文头信息 *************** */
    public static final String H_CUST_NO = "H_CUST_NO";// 客户号--渠道统一客户标记
    public static final String H_CIF_NO = "H_CIF_NO";// 核心客户号
    public static final String H_APP_TYPE = "H_APP_TYPE";// 渠道编号
    public static final String CHAN_NO = "CHAN_NO";// 渠道编号
    public static final String H_TRANS_SEQ = "H_TRANS_SEQ";// 请求流水号
    public static final String H_TRANS_CODE = "H_TRANS_CODE";// 交易代号
    public static final String H_TELLER = "H_TELLER";// 虚拟柜员
    public static final String H_ORG = "H_ORG";// 虚拟机构
    public static final String H_LEGAL = "H_LEGAL";// 法人编号
    public static final String H_BUSI_CODE = "H_BUSI_CODE"; // 功能编号
    public static final String H_SESSION_ID = "H_SESSION_ID";//会话ID
    public static final String H_CLIENT_IP = "H_CLIENT_IP";//	客户端IP地址
    public static final String H_CLIENT_VER_NO = "H_CLIENT_VER_NO";//	客户端版本
    public static final String H_CLIENT_TYPE = "H_CLIENT_TYPE";//	客户端类型： IP: IPHONE PD: IPAD  AD: ANDROID  AP: ANDROID PAD
    public static final String H_CLIENT_OS = "H_CLIENT_OS";//	客户端操作系统 I:IOS  A:ANDROID
    public static final String H_CLIENT_INFO = "H_CLIENT_INFO";//	客户端详细信息  如魅族  小米等信息
    public static final String H_CLIENT_NO = "H_CLIENT_NO";//	设备号
    public static final String H_IDT_TYPE = "H_IDT_TYPE";//	证件类型
    public static final String H_IDT_NO = "H_IDT_NO";//	证件号码
    
    /* *************** 以下为logback日志配置信息 *************** */
    public static final String MDC_TRANS_SEQ = "MDC_TRANS_SEQ";		//	交易流水号
    public static final String MDC_CUST_NO   = "MDC_CUST_NO";	    //	客户号
    public static final String MDC_TRANS_CODE = "MDC_TRANS_CODE";	//	交易编码
    public static final String MDC_TRANS_NAME = "MDC_TRANS_NAME";	//	交易名称
}
