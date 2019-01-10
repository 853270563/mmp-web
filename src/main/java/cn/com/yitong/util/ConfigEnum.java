package cn.com.yitong.util;

/**
 * 一些常用的字典值
 * @author lc3@yitong.com.cn
 *
 */
public enum ConfigEnum {

    // 用户类型
    DICT_USER_TYP_ALL("0"), // 移动和后台用户
    DICT_USER_TYP_MOBILE("1"), // 移动用户
    DICT_USER_TYP_WEB("2"), // 后台用户

    // 会话类型
    SESSION_TYPE_NAME("session.type"), // 第三方接入会话
    SESSION_TYPE_THIRD("3"), // 第三方接入会话
    SESSION_AUTH_STATUS_LOGINED("1"), // 第三方接入会话

	// 码表类型
	TRAN_DECARD_SIGN_STATE("SIGN_STATE"),	// 业务状态
	TRAN_DECARD_CARD_TYPE("CARD_TYPE"),		// 卡类型编号
	TRAN_DECARD_CERT_TYPE("CERT_TYPE"),		// 认证类型
	
	// 开卡业务状态。0：未提交；1：已提交，未审批；2：正在审批；3：正在审批，待补件；6：已取消7：审批通过；8：审批未通过；9：审批通过，但开卡交易执行失败
	DICT_TRAN_DECARD_SIGN_STATE_SUBMIT("1"),
	DICT_TRAN_DECARD_SIGN_STATE_APPROVING("2"),
	DICT_TRAN_DECARD_SIGN_STATE_APPROVING_PATCH("3"),
	DICT_TRAN_DECARD_SIGN_STATE_CANCELED("6"),
	DICT_TRAN_DECARD_SIGN_STATE_SUCCESS("7"),
	DICT_TRAN_DECARD_SIGN_STATE_FAILURE("8"),
	
	// 开卡业务 已查阅状态
	DICT_TRAN_DECARD_READ_STATE_HAS("1"),	// 未查阅
	DICT_TRAN_DECARD_READ_STATE_NO("0"),	// 已查阅
	
	//	交易类型代码
	DICT_TRAN_CODE_OPEN_CARD("01"),	//开借记卡
	
	CRDT_TRAN_CODE_OPEN_CARD("02"),	//信用卡开卡

    // 交易类别
    DICT_TRAN_OPERLOG_TRAN_TYPE_OPENCARD("1"), // 开卡日志

    // 影像格式类型
	DICT_ATTA_TYPE_IMAGE("0"),	//图片
	DICT_ATTA_TYPE_VIDEO("1")	//视频
	;
	private String strVal;
	public String strVal() {
		return this.strVal;
	}
	private ConfigEnum(String strVal) {
		this.strVal = strVal;
	}
}
