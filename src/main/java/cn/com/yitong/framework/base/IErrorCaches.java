package cn.com.yitong.framework.base;

import java.util.Map;


public interface IErrorCaches {
	public void init();
	/**
	 * 加载网银錯誤碼
	 */
	public void initErrorCode();
	/**
	 * 加载第三方系統錯誤碼映射
	 */
	public void initErrorMapping();
	/**
	 * 
	 * @param language
	 * @param code
	 * @return
	 */
	public String getErrMsgbyCode(String language, String code);
	
	public String getErrMsgbyRefcode(String language, String refcode);
	
	public Map getErrInfobyRefcode(String language, String refcode);
}
