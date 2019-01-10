package cn.com.yitong.ares.login.service;

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
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getMenuA(Map<String, Object> params);
	/**
	 * P_MOBI_ROL_MENU、P_MOBI_MENU
	 * B面菜单查询
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getMenuB(Map<String, Object> params);
	/**
	 * RATE_INTE
	 * 利率查询
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getRate(Map<String, Object> params);
	/**
	 * EXCHANGE_RATE
	 * 汇率查询
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getExRate(Map<String, Object> params);
	/**
	 * CHARGE
	 * 资费查询
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getCharge(Map<String, Object> params);
	/**
	 * RES_DATA
	 * 广告、展架查询
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getResFiles(Map<String, Object> params);
	/**
	 * RES_DATA
	 * 广告、展架查询
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> isUpdate(String Udate);
	
}
