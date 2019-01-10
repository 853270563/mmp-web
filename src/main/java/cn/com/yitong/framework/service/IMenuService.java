package cn.com.yitong.framework.service;

import java.util.List;
import java.util.Map;

import cn.com.yitong.framework.core.vo.MenuInfo;

/**
 * 网银系统菜单相关数据服务
 * 
 * @author yaoym
 * 
 */
public interface IMenuService {

	/**
	 * 加载网银系统菜单
	 * 
	 * @param appId
	 * @return
	 */
	public List<MenuInfo> findAllMenusByAppId(String appId);
	
	/**
	 * 加载网银系统网站地图菜单
	 * 
	 * @return
	 */
	public List findMenu4Map(Map params);
}
