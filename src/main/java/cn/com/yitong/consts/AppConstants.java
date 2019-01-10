package cn.com.yitong.consts;

import cn.com.yitong.framework.servlet.ServerInit;

/**
 * 系统常量类
 *
 * @author yaoyimin
 *
 */
public class AppConstants {

	public static final String TRUE = "true";
	public static final String FALSE = "false";

	public static final boolean DEBUG = true;

	// 当前应用为个人网银
	public static final String APP_ID = Properties.getString("APP_ID");
	public static final String APP_VERSION = Properties
			.getString("APP_VERSION");

	// 使用静态档板的交易码
	public static final String TRANSCODE_USE_STATIC_DATA = Properties
			.getString("TRANSCODE_USE_STATIC_DATA");
	
	
	public static final String IMG_WEB_URL = Properties
			.getString("IMG_WEB_URL");
	
	/**
	 * 通过HTTP SOAP连接进件系统所用的网关的开发环境的HTTP地址
	 */	
	public static final String CREDIT_GATEWAY_IP_PORT = Properties
			.getString("CREDIT_GATEWAY_IP_PORT");
	
	//推送url
	public static final String mqtt_url = Properties
			.getString("mqtt_url");
	public static final String appId = Properties
			.getString("appId");
	//推送url
	public static final String MQTT_IP    = ServerInit.getString(("MQTT_IP"));
	public static final String MQTT_PORT  = ServerInit.getString(("MQTT_PORT"));
	public static final String MQTT_NAME  = ServerInit.getString(("MQTT_NAME"));
	public static final String MQTT_PASSWD =ServerInit.getString(("MQTT_PASSWD"));
	//日期格式
	public static final String DATE_FORMATTER = Properties
			.getString("DATE_FORMATTER");
	//上图资源路径
	public static final String upload_files_path = Properties
				.getString("upload_files_path");
	//强制单次检查的交易清单
	public static final String TRANS_SIGNLE_CHECK = Properties
				.getString("TRANS_SIGNLE_CHECK");
	//动帐交易号
	public static final String TRANS_FLG_ANSY = Properties
			.getString("TRANS_FLG_ANSY");
	//登录相关交易号
	public static final String TRANS_FLG_LOGIN = Properties
			.getString("TRANS_FLG_LOGIN");
	//服务名
	public static final String server_address = Properties
			.getString("server_address");
	//配置文件
		public static final String IBS_CONF = Properties
				.getString("IBS_CONF");
	public static final String AUDI_STAT = "4"; // 数据审核状态
	// 服务所在主机ip地址
	public static final String PRO_RELEASE_HOST = Properties
			.getString("pro_release_host");
	
	// ftp
	public static final String SFTP_HOST = Properties
				.getString("FTP_DOWN_HOST");
	public static final int SFTP_PORT = Properties
			.getInt("FTP_DOWN_PORT");
	public static final String SFTP_USER = Properties
			.getString("FTP_DOWN_USER_NAME");
	public static final String SFTP_PW = Properties
			.getString("FTP_DOWN_USER_PW");
	/**
	 * 客户端上送报文目录
	 */
	public static final String CLIENT_MSG =Properties
			.getString("CLIENT_MSG");
	
	/**
	 * 资源服务器地址
	 */
	public static String RESOURCES_URL=Properties
			.getString("RESOURCES_URL");;
	
			
	/**
	 * 影像目录
	 */
	public static final String CRDT_IMAGE =Properties
			.getString("CRDT_IMAGE");


    public static final String TEST_FILE_PATH=Properties.getString("TEST_FILE_PATH");

	// 当前应用为公共信息
	public static final String APP_PUB = "PUB";

	public static final String JSON_START_WITH = "{";

	public static final String ZH_CN = "CN";
	public static final String ZH_HK = "HK";
	public static final String EN = "EN";

	public static final String ERR_CODE = "ERR_CODE";// 錯誤碼
	public static final String ERR_MSG = "ERR_MSG";// 錯誤信息
	public static final String REF_ERR_CODE = "REF_ERR_CODE";// 來源系統code

	public static final String RESPONSE_CODE = "rspCode";
	public static final String RESPONSE_MSG = "rspDesc";

	public static final String AAAAAA = "000000";

	public static final String STATUS = "STATUS";
	public static final String LAST_LGN_DATE = "LAST_LGN_DATE";
	public static final String LAST_LGN_TIME = "LAST_LGN_TIME";
	public static final String MSG = "MSG";
	public static final String DESC = "desc";
	public static final String XML_MAP = "Map";

	/** 交易正常 */
	public static final String STATUS_OK = "1";
	/** 交易异常 */
	public static final String STATUS_FAIL = "0";
	public static final String STATUS_RANDOM_ERROR = "2";
	public static final String STATUS_PWD_ERROR = "3";
	public static final String STATUS_USERNAME_NULL_ERROR = "4";
	public static final String STATUS_USER_ALREADY_EXIST_ERROR = "5";
	public static final String STATUS_USER_ERROR = "6";
	public static final String STATUS_USER_LOCKED_ERROR = "7";
	public static final String STATUS_USER_INIT_PWD = "8";//当先为初始密码，请修改
	/** 重复提交，错误码将被过滤 */
	public static final String STATUS_RESUBMIT = "100";
	/** 用戶沒有進入沒有登錄時進行的操作session超時狀態 */
	public static final String STATUS_FAIL_SESSIONERROR = "-11";
	public static final String MSG_FAIL = "交易失败!";
	public static final String MSG_RANDOM_ERROR = "验证码错误!";
	public static final String MSG_PWD_ERROR = "密码错误!";
	public static final String MSG_USER_ERROR = "用户或密码不正确!";
	public static final String MSG_USER_LOCKED_ERROR = "输入的密码错误次数已达到5次";
	public static final String MSG_SUCC = "交易成功!";
	public static final String MSG_USERNAME_NULL_ERROR = "用户名不能为空";
	public static final String MSG_USER_ALREADY_EXIST_ERROR = "用户已经是登录状态";
	public static final String SESSION_LOGIN_INFO = "Session_Login_info";

	// 未设置的账号别名
	public static final String EMPTY_ACCT_ALIAS = "COMM.EMPTY_ALIAS";
	// 内容分隔符
	public static final char SPIT_CHAR = 29;
	public static final String SPIT_CODE = "#";
	// UPD的IP
	public static final String UPD_IP = "UPD_IP";
	public static final String UPD_IP2 = "UPD_IP2";

	public static final String UPD_POPT = "UPD_PORT";
	public static final String UPD_POPT2 = "UPD_PORT2";
	public static final String USER_LOGIN = "USER_LOGIN";

	public static final String CHANEL_JSON = "json";
	public static final String CHANEL_JXML = "jxml";
	public static final String DAY_MAX_LMT = "DAY_MAX_LMT";// 附属户每日累计支出限额
	public static final String OTH_QUOTA = "OTH_QUOTA";// 第三方转账每日累计限额
	public static final String BOC_QUOTA = "BOC_QUOTA";// 附属户每日累计支出限额
	public static final String LIMIT_CACHES = "LMT";

	/**
	 * 已发布
	 */
	public static final String AUDI_STAT_PUBLISHED = "4";
	public static final String MIGRATE_FLG = "MIGRATE_FLG"; // 老网银别名迁移标识 0:不迁移
															// 1:迁移
	
	
	
	/**
	 * 影像上送路径
	 * 
	 */
	public static final String UPLOAD_FILES_PATH = Properties.getString("upload_files_path");
	
	public  final int PWD_MAX_ERR_NUM = Properties.getInt("PWD_MAX_ERR_NUM");

    /**
     * 微信 appid 和 app secret
     */
    public static final String push_type = Properties.getString("push_type");

    public static final String wx_appId = Properties.getString("wx_appId");
    public static final String wx_appsecret =  Properties.getString("wx_appsecret");
    public static final String wx_token =  Properties.getString("wx_token");

    public static String AccessToken="";
    public static String accessTokenUrl = Properties.getString("accessTokenUrl");
    //"https://api.weixin.qq.com/cgi-bin/token";
    public static String publishUrl = Properties.getString("publishUrl");
    //"https://api.weixin.qq.com/cgi-bin/menu/create?access_token=";
    public static String getUserInfoUrl = Properties.getString("getUserInfoUrl");
    //"https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s";
    public static String KeFuPostMsg = Properties.getString("KeFuPostMsg");
    //"https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s";
    public static String TemplateMsg = Properties.getString("TemplateMsg");
    //public static String TemplateMsg = "https://mp.weixin.qq.com/advanced/tmplmsg?action=faq&token=%s&lang=zh_CN";
    //"https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
    public static String QunFaPostMsg = Properties.getString("QunFaPostMsg");
    //"https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=%s";


}
