package cn.com.yitong.framework.net.impl.mb;

class EBankConst {

	// 报文类型
	public static final String FILED_TYPE_C = "C"; // 字符
	public static final String FILED_TYPE_N = "N"; // 数值
	public static final String FILED_TYPE_E = "E"; // 循环
	public static final String FILED_TYPE_B = "B"; // 加密密文

	public static final String TYPE_MAP = "map";
	public static final String TYPE_LIST = "list";
	
	public static final String MB_SERVICE = "MB_SERVICE";

	public static final String XT_ROOT = "EcipService";
	public static final String XT_HEAD = "EcipService_Header";
	public static final String XT_BODY = "EcipService_Body";
	public static final String XT_ITEM = "field";
	public static final String XT_LIST = "field-list";

	public static final String AT_NAME = "name";
	public static final String AT_ESB_NAME = "esbName";// ESB接口服务名
	public static final String AT_REQ_SERVICE = "requestBody";// IBCP接口編號
	public static final String AT_RSP_SERVICE = "responseBody";// IBCP接口編號

	public static final String AT_TARGET_NAME = "client";
	public static final String AT_HEADER = "header";
	public static final String AT_DESC = "desc";
	public static final String AT_TYPE = "type";
	public static final String AT_LEN = "length";
	public static final String AT_REQUIRED = "required";
	public static final String AT_EXCHANGE = "exchange";
	public static final String AT_DEFVAL = "default";
	public static final String AT_XPATH = "xpath";
	public static final String AT_PLUS = "plugin";
	public static final String AT_ITEM = "item";// 上送list時，item名

	public static final String ESB_NAME = "esbName";// 上送list時，item名
	public static final String IBCP_NAME = "ibcpName";// 上送list時，item名

	// 存储记录长度
	public static final String AT_LIST_SIZE = "sizeField";
	// 存储MAP结构名称
	public static final String AT_RECORD_FIELD = "recordField";
	// 数据字典集合
	public static final String AT_MAPKEY = "mapKey";
	// 数据解析字段
	public static final String AT_MAPKEY_DESCNAME = "descName";

	public static final String XT_SEND = "snd";
	public static final String XT_RCV = "rcv";
	
	// 预览
	public static final String PREVIEW_FLAG = "PREVIEW_FLAG";

	public static final String EMPTY = "";

	public static final int ONICE_TRAN_LIMIT = 20000;
	public static final int ONEDAY_TRAN_LIMIT = 50000;

	public static final String SUCCESS = "N";
}