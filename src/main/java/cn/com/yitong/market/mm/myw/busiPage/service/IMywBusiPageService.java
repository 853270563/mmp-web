package cn.com.yitong.market.mm.myw.busiPage.service;

import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 业务包管理
 * 
 * @author ysh
 * 
 */
public interface IMywBusiPageService {

	/**
	 * BUSI_PAK_ORDER
	 * 删除业务包排序表数据
	 * @param params
	 * @return
	 */
	public boolean deleteBPO(Map<String, Object> params);
	
	/**
	 * BUSI_PKG_SEL
	 * 删除业务包中的业务数据
	 * @param params
	 * @return
	 */
	public boolean deleteBPS(Map<String, Object> params);
	
	/**
	 * BUSI_PKG
	 * 更新业务包数据
	 * @param params
	 * @return
	 */
	public boolean updateById(Map<String, Object> params);
	
	/**
	 * BUSI_PAK_ORDER
	 * 添加排序数据
	 * @param params
	 * @return
	 */
	public boolean insertBPO(Map<String, Object> params);
	
	/**
	 * BUSI_PKG_SEL
	 * 增加包业务关联表
	 * @param params
	 * @return
	 */
	public boolean insertPageBusiConf(Map<String, Object> params);
	
	/**
	 * BUSI_PKG
	 * 增加业务包数据
	 * @param params
	 * @return
	 */
	public boolean insert(Map<String, Object> params);
	
	/**
	 * BUSI_PKG
	 * 删除业务包数据
	 * @param params
	 * @return
	 */
	public boolean delete(Map<String, Object> params);
	
	/**
	 * 
	 * 获取业务包json数据
	 * @param params
	 * @return
	 */
	public JSONArray getJsonStr(String userId);
	
}
