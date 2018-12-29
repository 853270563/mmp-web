package cn.com.yitong.modules.ares.login.service;

import java.util.List;
import java.util.Map;

/**
 * 业务包管理
 * 
 * @author ysh
 * 
 */
public interface ILoginExpandDataService {
	
	/**
	 * P_MOBI_ROL_MENU、P_MOBI_MENU
	 * A面菜单查询
	 */
	public List<Map<String,Object>> getMenuA(Map<String, Object> params);
	/**
	 * P_MOBI_ROL_MENU、P_MOBI_MENU
	 * B面菜单查询
	 */
	public List<Map<String,Object>> getMenuB(Map<String, Object> params);
}
