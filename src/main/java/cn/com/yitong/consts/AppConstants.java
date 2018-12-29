package cn.com.yitong.consts;

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

	// 使用静态档板的交易码
	public static final String TRANSCODE_USE_STATIC_DATA = Properties.getString("TRANSCODE_USE_STATIC_DATA");
	public static final String IMG_WEB_URL = Properties.getString("IMG_WEB_URL");	//资源下载路径

	public static final String plat_files_path = Properties.getString("plat_files_path");//共享存储路径
	public static final String upload_files_path = Properties.getString("upload_files_path");//上图资源路径
	public static final String PRO_RELEASE_HOST = Properties.getString("pro_release_host");// 服务所在主机ip地址
    public static final String TEST_FILE_PATH = Properties.getString("TEST_FILE_PATH"); //测试报文保存路径
	public static final String JSON_START_WITH = "{";
	public static final String ZH_CN = "CN";
	public static final String ZH_HK = "HK";
	public static final String EN = "EN";
	public static final String XML_MAP = "Map";

	//交易结果标识------------begin---------------
	public static final String STATUS = "STATUS";
	public static final String MSG = "MSG";
	public static final String STATUS_OK = "1";	//交易正常
	public static final String STATUS_FAIL = "0";	//交易异常
	public static final String MSG_FAIL = "交易失败!";
	public static final String MSG_NOT_FOUND = "交易不存在!";
	public static final String MSG_RANDOM_ERROR = "验证码错误!";
	public static final String MSG_SUCC = "交易成功!";
	//交易结果标识------------end---------------

	// 内容分隔符
	public static final char SPIT_CHAR = 29;
	public static final String SPIT_CODE = "#";
}
