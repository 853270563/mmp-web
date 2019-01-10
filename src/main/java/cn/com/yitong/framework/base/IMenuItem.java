package cn.com.yitong.framework.base;

import java.util.List;

import cn.com.yitong.framework.core.vo.MenuInfo;
import cn.com.yitong.framework.core.vo.MenuItem;

public interface IMenuItem {

	/**
	 * 菜单名称
	 * 
	 * @param language
	 * @return
	 */
	public abstract String getMenuName(String language);

	/**
	 * 菜单编号
	 * 
	 * @return
	 */
	public abstract String getMenuId();

	/**
	 * 链接
	 * 
	 * @return
	 */
	public abstract String getHref();

	/**
	 * TAB缓存名称
	 * 
	 * @return
	 */
	public abstract String getTabName();

	/**
	 * 菜单信息
	 * 
	 * @return
	 */
	public abstract MenuInfo getMenu();

	public abstract void setMenu(MenuInfo menu);

	/**
	 * 父菜单
	 * 
	 * @return
	 */
	public abstract IMenuItem getParent();

	public abstract void setParent(IMenuItem parent);

	/**
	 * 子菜单
	 * 
	 * @return
	 */
	public abstract List<MenuItem> getChildren();

	public abstract void setChildren(List<MenuItem> children);

	public boolean hasChildren();

	/**
	 * 登录认证类型
	 * 
	 * @return
	 */
	public String getAuthType();

	/**
	 * 用户类型
	 * 
	 * @return
	 */
	public String getUserType();

	/**
	 * 是否开放用户，白名单
	 * 
	 * @return
	 */
	public boolean openUser();

}