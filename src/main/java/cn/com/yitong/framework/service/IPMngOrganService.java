package cn.com.yitong.framework.service;

import java.util.List;
import java.util.Map;

/**
 * 机构管理
 * 
 * @author 刘庆
 * 
 */
public interface IPMngOrganService {

    /**
     * 通过机构ID查询对应的机构
     * @param orgId
     * @return
     */
    Map<String, Object> queryById(String orgId);

    /**
	 * 查询 ZH
	 * 
	 * @param map
	 * @return
	 */
	public List findList_ZH(Map<String, String> map);
	/**
	 * 
	 * @param map
	 * @return
	 */
	public List findList_HK(Map<String, String> map);
	/**
	 * 
	 * @param map
	 * @return
	 */
	public List findList_EN(Map<String, String> map);

	/**
	 * 删除
	 * 
	 * @param map
	 * @return
	 */
	public boolean deleteOrgan(Map<String, String> map);

}
