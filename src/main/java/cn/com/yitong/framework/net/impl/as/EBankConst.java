package cn.com.yitong.framework.net.impl.as;


/**
 * 
 <!--param元素属性说明 --> <!--type:元素字段的数据类型 --> <!--length:元素字段在包中的长度 -->
 * <!--align:元素字段不够长度时是左补字符(right)还是右补字符(right) --> <!--fill:所补字符，为空格、可见的ASCII字段
 * --> <!--default:元素字段的默认值 --> <!--array元素属性说明 --> <!--name:信息组名称 -->
 * <!--size:信息组的最大数组长 --> <!--eve:信息组单组信息包长度 -->
 * <!--exchange:信息组大小在数据包中是否可变，N表示不可变，以size做为其长度，Y表示可变，以array前的字段param字段的信息为准
 * -->
 * 
 * @author yaoym
 * 
 */
class EBankConst {

	// 报文类型
	public static final String FILED_TYPE_C = "C"; // 字符串
	public static final String FILED_TYPE_D = "D"; // double类型
	public static final String FILED_TYPE_CD = "CD"; // CD对象
	public static final String FILED_TYPE_AC = "AC"; // 账号	
	public static final String FILED_TYPE_N = "N"; // 数值
	public static final String FILED_TYPE_O = "M"; // 对象（非循环）
	public static final String FILED_TYPE_E = "E"; // 循环
	public static final String FILED_TYPE_B = "B"; // 加密密文
	public static final String FILED_TYPE_IMAGE = "IMAGE"; // 加密密文
	public static final String FILED_PARAM_SESSION = "@session"; // 用户session
	public static final String FILED_PARAM_STATIC =  "@static";	 // 静态数组
	public static final String FILED_PARAM_DYNAMIC = "@dynamic"; // 动态数组
	public static final char FIX_END = 0x1F;

	public static final String TYPE_MAP = "map";
	public static final String TYPE_LIST = "list";

	public static final String XT_ITEM = "field";
	public static final String XT_LIST = "field-list";

	public static final String AT_NAME = "name";
	public static final String AT_TARNAME = "targetName";
	public static final String AT_CLIENT_NAME = "client";
	public static final String AT_HEADER = "header";
	public static final String AT_DELIMITER_HEADER = "delimiter";
	public static final String AT_DELIMITER_SND = "sndDelimiter";
	public static final String AT_DELIMITER_REV = "rcvDelimiter";

	public static final String AT_DELIMITER_ROW = "rowDdelimiter";
	public static final String AT_DELIMITER_FIELD = "fieldDelimiter";

	public static final String AT_DESC = "desc";
	public static final String AT_TYPE = "type";
	public static final String AT_LEN = "length";
	public static final String AT_DOLT = "dolt";
	public static final String AT_REQUIRED = "required";
	public static final String AT_EXCHANGE = "exchange";
	public static final String AT_DEFVAL = "default";
	public static final String AT_XPATH = "xpath";
	public static final String AT_PLUS = "plugin";
	public static final String AT_DATATYP = "dataTyp";
	// 数据字典集合
	public static final String AT_EXCODE = "excode";
	public static final String AT_MAPKEY = "mapKey";
	// 数据解析字段
	public static final String AT_MAPKEY_DESCNAME = "descName";
	
	public static final String EMPTY = "";

	public static final int ONICE_TRAN_LIMIT = 20000;
	public static final int ONEDAY_TRAN_LIMIT = 50000;
	
	public static final String SUCCESS_RETURN_CODE="00";
	public static final String FAIL_RETURN_CODE="0";

	public static final int LENGTH = 8;

	public static final String CORE_SERVER_URL = "CORE_SERVER_URL";
	//数据结构
	public static final String SND = "snd";
	public static final String RCV = "rcv";
	public static final String SND_HEAD = "head";
	public static final String RCV_HEAD = "head";

	public static final String SND_BODY = "body";
	public static final String RCV_BODY = "body";

}