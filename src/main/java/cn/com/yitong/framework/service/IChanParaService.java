package cn.com.yitong.framework.service;

import java.util.List;
import java.util.Map;


public interface IChanParaService {
	/**
	 * 查詢安全問題
	 */
	
	public List findSecu_Question();
	
	/**
	 * 查詢自助機繳費類別
	 */
	/**
	 * 查詢繳費單卡片
	 * 
	 * @param param
	 * @return
	 */
	public List FINDALL_PAY_FEE_TYP(Map<String, String> map) ;
	/**
	 * 通過積分獲取等級
	 */
	public List findGradeByIgt(String igt);
	
	/**
	 * 查詢旅游保险参数
	 */
	public List findInsurParam(Map<String, String> map) ;
	
	/**
	 * 查詢参数通过类型
	 */
	public List findParamByType(String type) ;
	
	/**
	 * 查詢旅游保险参数通过类型
	 */
	public List findLFHByType(String type) ;
	/**
	 * 查詢参数通过类型HK
	 */
	public List findParamByTypeHK(String type) ;
	/**
	 * 查詢参数通过类型CN
	 */
	public List findParamByTypeCN(String type) ;
	/**
	 * 查詢参数通过类型EN
	 */
	public List findParamByTypeEN(String type) ;
	
	/**
	 * 查詢参数详情通过类型HK
	 */
	public List findParamDetailByTypeHK(String type) ;
	/**
	 * 查詢参数详情通过类型CN
	 */
	public List findParamDetailByTypeCN(String type) ;
	/**
	 * 查詢参数详情通过类型EN
	 */
	public List findParamDetailByTypeEN(String type) ;
	
	
	/**
	 * 查詢證件類型
	 * @param type
	 * @return
	 */
	public List findIDTypeByType(String type);
	
	/**
	 * 獲取定期存款期限 EN 
	 * @param type
	 * @return
	 */
	public List findFDTypeByType_EN(String type);
	
	/**
	 * 獲取定期存款期限 CN 
	 * @param type
	 * @return
	 */
	public List findFDTypeByType_CN(String type);
	
	/**
	 * 獲取定期存款期限 HK 
	 * @param type
	 * @return
	 */
	public List findFDTypeByType_HK(String type);
	
	/**
	 *  根據幣種獲取定期存款期限 HK
	 * @param ccy_no
	 * @return
	 */
	public List findFDByType_HK(String ccy_no);
	
	/**
	 *  根據幣種獲取定期存款期限 CN
	 * @param ccy_no
	 * @return
	 */
	public List findFDByType_CN(String ccy_no);
	
	/**
	 *  根據幣種獲取定期存款期限 EN
	 * @param ccy_no
	 * @return
	 */
	public List findFDByType_EN(String ccy_no);
	
	/**
	 *  獲取AASTOCKS密碼
	 * @param ccy_no
	 * @return
	 */
	public String findStreamPwd(String pwd);
	
	/**
	 * 獲取定期幣別HK
	 * @param type
	 * @return
	 */
	public List findFDCcyTyp_HK(String type);
	/**
	 * 獲取定期幣別EN
	 * @param type
	 * @return
	 */
	public List findFDCcyTyp_CN(String type);
	/**
	 * 獲取定期幣別EN
	 * @param type
	 * @return
	 */
	public List findFDCcyTyp_EN(String type);
	
	/**
	 * 获取多币宝
	 * @param map
	 * @return
	 */
	public List findMultiByType(Map<String, String> map);
}
