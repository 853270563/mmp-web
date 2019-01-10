package cn.com.yitong.framework.net.impl;
/**
 * @author luanyu
 * @date   2018年8月9日
 */
public class NetConst {
	public static String UTF_8 = "UTF-8";

	// 报文类型
	public static final String FILED_TYPE_C = "C"; // 字符
	public static final String FILED_TYPE_N = "N"; // 整数
	public static final String FILED_TYPE_M = "M"; // Map
	public static final String FILED_TYPE_E = "E"; // 循环
	public static final String FILED_TYPE_TPWD = "T"; // 交易密码
	public static final String FILED_TYPE_LPWD = "L"; // 登录密码

	public static final String TYPE_MAP = "map";
	public static final String TYPE_LIST = "list";

	public static final String XT_ITEM = "field";
	public static final String XT_LIST = "field-list";

	public static final String AT_NAME = "name";
	public static final String AT_TARGET = "target";
	public static final String AT_HEADER = "header";
	public static final String AT_DESC = "desc";
	public static final String AT_TYPE = "type";
	public static final String AT_LEN = "length";
	public static final String AT_REQUIRED = "required";
	public static final String AT_DEFAULT = "default";
	public static final String AT_XPATH = "xpath";
	public static final String AT_PLUS = "plus";
	public static final String AT_SIZE_FIELD = "sizeField";
	public static final String AT_COMMENT = "comment";
	public static final String AT_MAPKEY = "mapKey"; // 数据字典集合
	public static final String AT_MAPKEY_DESCNAME = "descName"; // 数据解析字段

	public static final String XT_SEND = "snd";
	public static final String XT_RCV = "rcv";

	public static final String EMPTY = "";

	// 分页
	public static final String FILE_NAME = "fileName";// 定义文件中的增加文件头
	public static final String FILE_ROWS = "fileRows";// 定义文件中的增加文件头
	public static final String NEXT_KEY = "NEXT_KEY";// 下一页
	public static final String PAGE_SIZE = "PAGE_SIZE";// 每页数目

	public static final String NEXT_KEY_DEFAULT = "1";
	public static final String PAGE_SIZE_DEFAULT = "5";
}
