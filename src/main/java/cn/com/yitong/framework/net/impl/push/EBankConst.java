package cn.com.yitong.framework.net.impl.push;


class EBankConst {

	// 报文类型
	public static final String FILED_TYPE_C = "C"; // 字符
	public static final String FILED_TYPE_N = "N"; // 数值
	public static final String FILED_TYPE_E = "E"; // 循环
	public static final String FILED_TYPE_B = "B"; // 加密密文

	public static final String TYPE_MAP = "map";
	public static final String TYPE_LIST = "list";

	public static final String XT_ROOT = "service";  
	
	public static final String XT_ITEM = "field";
	public static final String XT_LIST = "field-list";

	public static final String AT_NAME = "name";
	public static final String AT_TARGET_NAME = "targetName";
	public static final String AT_HEADER = "header";
	public static final String AT_DESC = "desc";
	public static final String AT_TYPE = "type";
	public static final String AT_LEN = "length";
	public static final String AT_REQUIRED = "required";
	public static final String AT_EXCHANGE = "exchange";
	public static final String AT_DEFVAL = "default";
	public static final String AT_XPATH = "xpath";
	public static final String AT_PLUS = "plugin";
	public static final String AT_SIZE_FIELD = "sizeField";
	public static final String AT_ROW = "row";
	// 数据字典集合
	public static final String AT_MAPKEY = "mapKey";
	// 数据解析字段
	public static final String AT_MAPKEY_DESCNAME = "descName";

	public static final String XT_SEND = "snd";
	public static final String XT_RCV = "rcv"; 

	public static final String EMPTY = "";

	public static final int ONICE_TRAN_LIMIT = 20000;
	public static final int ONEDAY_TRAN_LIMIT = 50000;
	
	//报文相关字段
	public static final String TRAN_DATE="TRAN_DATE";
	public static final String TRAN_TIME="TRAN_TIME";
	public static final String IBS_LGN_ID="IBS_LGN_ID";
	public static final String CIF_NO="CIF_NO";
	public static final String TRAN_SEQ = "TRAN_SEQ";
	public static final String AUTH_CODE = "AUTH_CODE";
	public static final String LAGG = "LAGG";
	
	// 成功的返回码
	public static final String RSP_CODE_SUCCESS="00000";
	
	/******  MQ Environment相关 ****
	 *  qmName=MB8QMGR
		inQName=Q_PUSH_IN
		outQName=Q_PUSH_OUT
		MQHostName=172.31.252.205
		MQPort=1415
		MQchannel=CHANL_PUSH
		MQCCSID=1208
	 **/
	
	
	/****** MQ 相关 ******/
}