package cn.com.yitong.framework.service;

import java.util.List;
import java.util.Map;

/**
 * 客户应用版本管理
 * 
 * @author yaoym
 * 
 */
public interface IAppVersionService {

	/**
	 * 加载当前版本信息
	 * 
	 * @param params
	 * @return
	 */
	public Map loadCurrent(Map params);

	/**
	 * 加载历史版本详情
	 * 
	 * @param temp
	 * @return
	 */
	public List findHistory(Map temp);

}
