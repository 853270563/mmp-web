package cn.com.yitong.framework.net.impl.esb;

import java.util.Arrays;
import java.util.List;

import cn.com.yitong.consts.Properties;

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
public class ESBConst {
	public static final String UTF_8 = "UTF-8"; // 字符
	// 报文类型
	public static final String FILED_TYPE_C = "C"; // 字符
	public static final String FILED_TYPE_T = "T"; // 字符
	public static final String FILED_TYPE_AC = "AC"; // 账号	
	public static final String FILED_TYPE_N = "N"; // 数值
	public static final String FILED_TYPE_S = "S"; // 静态  有子元素但不循环
	public static final String FILED_TYPE_E = "E"; // 循环
	public static final String FILED_TYPE_L = "L"; //左补位
	public static final String FILED_TYPE_D = "D"; // 
	public static final String FILED_TYPE_B = "B"; // 加密密文
	public static final String FILED_TYPE_P = "P";
	public static final char FIX_END = 0x1F;

	public static final String TYPE_MAP = "map";
	public static final String TYPE_LIST = "list";
	public static final String TYPE_LIST_HEAD = "LIST_HEAD";
	public static final String TYPE_LIST_BODY = "LIST_BODY";

	public static final String XT_ITEM = "field";
	public static final String XT_head = "head";
	public static final String XT_body = "body";
	public static final String XT_LIST = "field-list";
	public static final String AT_CLIENT = "client";
	public static final String AT_NAME = "name";
	public static final String AT_TARGET_NAME = "targetName";
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
	public static final String AT_SIZE_FIELD = "sizeField";
	public static final String AT_ENCRYPT ="encryption";
	public static final String  AT_NUM  ="num";
	public static final String  AT_LOOP_NUM  ="loop_num";
	public static final String  AT_P_TYPE ="p_type";
	public static final String AT_ISCXT = "isCtx";
	// 数据字典集合
	public static final String AT_MAPKEY = "mapKey";
	// 数据解析字段
	public static final String AT_MAPKEY_DESCNAME = "descName";
	
	public static final String ELEMENT_ROOT = "Service";
	public static final String ELEMENT_HEAD = "Service_Header";
	public static final String ELEMENT_BODY = "Service_Body";
	public static final String ELEMENT_BODY_RESPONSE = "response";
	public static final String ELEMENT_BODY_SERVICE_RESPONSE = "service_response";
	
	/**
	 * @FieldName: ELEMENT_TYPE_LIST
	 * @FieldType: String
	 * @Description: 节点类型为List
	 */
	public static final List<String> ELEMENT_TYPE_LIST = Arrays.asList(
			"record", "ResultSet", "CM-DOC-GRP");
	/**
	 * @FieldName: ELEMENT_TYPE_MAP
	 * @FieldType: String
	 * @Description: 节点类型为Map
	 */
	public static final List<String> ELEMENT_TYPE_MAP = Arrays.asList("Line",
			"response", "service_response", "INM-PAGE-CTL", "request",
			"OPM-PAGE-CTL", "ext_attributes");

	
	public static final String XT_SEND = "snd";
	public static final String XT_RCV = "rcv";

	public static final String XT_SEND_HEAD = "snd-header";
	public static final String XT_RCV_HEAD = "rcv-header";

	public static final String EMPTY = "";

	public static final int ONICE_TRAN_LIMIT = 20000;
	public static final int ONEDAY_TRAN_LIMIT = 50000;
	
	public static final String SUCCESS_RETURN_CODE="00";

	public static final int LENGTH = 7;

	public static final String CORE_SERVER_URL = "CORE_SERVER_URL";
	/** 接口返回报文头 */
	public static final String RS_COMPLETE = "COMPLETE";
	public static final String RS_STATUS = "status";
	public static final String RS_DESC = "desc";
	public static final String RS_CODE = "code";
	/**
	 * esb 请求流水号前缀
	 */
	public static final String ESB_SERIVALSN = Properties.getString("ESB_SERIVALSN");
}