package cn.com.yitong.framework.core.vo;

import java.util.List;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IMenuItem;
import cn.com.yitong.util.StringUtil;

/**
 * 菜单对象
 * 
 * @author yaoym
 * 
 */
public class MenuItem implements IMenuItem {
	private MenuInfo menu;
	private IMenuItem parent;
	private List<MenuItem> children;

	public MenuItem(MenuInfo menu) {
		this.menu = menu;
	}

	@Override
	public String getMenuName(String language) {
		String name = "";
		if (AppConstants.EN.equals(language)) {
			name = menu.getNAME_EN();
		} else if (AppConstants.ZH_HK.equals(language)) {
			name = menu.getNAME_HK();
		} else {
			name = menu.getNAME_CN();
		}
		return StringUtil.isEmpty(name) ? getMenuId() : name;
	}

	@Override
	public String getHref() {
		return menu.getMENU_URL();
	}

	@Override
	public String getTabName() {
		return "";
	}

	@Override
	public MenuInfo getMenu() {
		return menu;
	}

	@Override
	public void setMenu(MenuInfo menu) {
		this.menu = menu;
	}

	@Override
	public IMenuItem getParent() {
		return parent;
	}

	@Override
	public void setParent(IMenuItem parent) {
		this.parent = parent;
	}

	@Override
	public List<MenuItem> getChildren() {
		return children;
	}

	@Override
	public void setChildren(List<MenuItem> children) {
		this.children = children;
	}

	@Override
	public String getMenuId() {
		// TODO Auto-generated method stub
		return menu.getMENU_ID();
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return children != null && !children.isEmpty();
	}

	@Override
	public String getAuthType() {
		return null;
	}

	@Override
	public String getUserType() {
		return null;
	}

	@Override
	public boolean openUser() {
		return "Y".equals(menu.getINCLUDE_AUTH());
	}

}
