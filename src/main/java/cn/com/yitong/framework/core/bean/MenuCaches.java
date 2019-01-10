package cn.com.yitong.framework.core.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IMenuCaches;
import cn.com.yitong.framework.base.IMenuItem;
import cn.com.yitong.framework.core.vo.MenuInfo;
import cn.com.yitong.framework.core.vo.MenuItem;
import cn.com.yitong.framework.service.IMenuService;
import cn.com.yitong.util.YTLog;

public class MenuCaches implements IMenuCaches {

	private Logger logger = YTLog.getLogger(this.getClass());
	@Autowired
	private IMenuService menuService;

	private Map<String, String> menuCaches = new HashMap<String, String>();

	public void init() {
		generyMenuHtmlCaches();
	}

	private void generyMenuHtmlCaches() {
		if (logger.isDebugEnabled()) {
			logger.debug("------------MenuCaches.menus start---------------");
		}
		// 加载网银菜单
		List<MenuInfo> menus = menuService
				.findAllMenusByAppId(AppConstants.APP_ID);
		List<MenuItem> menusCaches = gerneryTopMenus(menus);
		for (IMenuItem item : menusCaches) {
			gernerySubMenus(item, menus);
		}
		// 生成菜单缓存
		// 语言集
		String[] languags = { AppConstants.ZH_CN, AppConstants.ZH_HK,
				AppConstants.EN };
		// 开放用户标记集
		boolean[] openUserFlags = { true, false };

		for (String language : languags) {
			for (boolean openUser : openUserFlags) {
				String key = getMenuKey(language, openUser);
				StringBuffer bf = new StringBuffer();
				for (MenuItem menu : menusCaches) {
					generyMenuHtml(bf, menu, 1, language);
				}
				menuCaches.put(key, bf.toString());
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("------------MenuCaches.menus success---------------");
		}
	}

	/**
	 * 菜单键值
	 * 
	 * @param language
	 * @param openUser
	 * @return
	 */
	private String getMenuKey(String language, boolean openUser) {
		StringBuffer bf = new StringBuffer();
		bf.append(language).append("").append(openUser);
		return bf.toString();
	}

	private List<MenuItem> gerneryTopMenus(List<MenuInfo> menus) {
		List<MenuItem> items = new ArrayList<MenuItem>();
		List<MenuInfo> tmps = new ArrayList<MenuInfo>();
		// 分离第一级菜单
		for (MenuInfo menu : menus) {
			if ("1".equals(menu.getMENU_GRA())) {
				tmps.add(menu);
				continue;
			}
		}
		// 去除已缓存菜单
		menus.removeAll(tmps);
		for (MenuInfo menu : tmps) {
			if ("1".equals(menu.getMENU_GRA())) {
				MenuItem item = new MenuItem(menu);
				items.add(item);
			}
		}
		tmps = null;
		return items;
	}

	private boolean gernerySubMenus(IMenuItem menuItem, List<MenuInfo> menus) {
		List<MenuItem> children = new ArrayList<MenuItem>();
		List<MenuInfo> tmps = new ArrayList<MenuInfo>();
		for (MenuInfo menu : menus) {
			if (menuItem.getMenuId().equals(menu.getMENU_PAR_ID())) {
				tmps.add(menu);
				continue;
			}
		}
		// 去除已缓存菜单
		menus.removeAll(tmps);
		for (MenuInfo menu : tmps) {
			if (menuItem.getMenuId().equals(menu.getMENU_PAR_ID())) {
				MenuItem item = new MenuItem(menu);
				gernerySubMenus(item, menus);
				children.add(item);
				continue;
			}
		}
		menuItem.setChildren(children);
		tmps = null;

		return false;
	}

	private String generyMenuHtml(StringBuffer bf, IMenuItem menu, int lvl,
			String language) {
		bf.append("<li>");
		bf.append("<a ");
		gerneryMenuDetail(menu, language, lvl, bf);
		bf.append(">");
		bf.append(menu.getMenuName(language));
		bf.append("</a>");
		// 是否有子菜单
		gerneryMenuChildren(menu, language, lvl, bf);
		bf.append("</li>");
		return bf.toString();
	}

	private void gerneryMenuDetail(IMenuItem menu, String language, int lvl,
			StringBuffer bf) {
		if (lvl == 1) {
			if ("PP02001".equals(menu.getMenuId())) {// 首页
				bf.append("data-tab='first' href='#first' class='current'");
				return;
			} else if ("PP12000".equals(menu.getMenuId())) { // add stock by ypy
				bf.append("data-tab='stock' href='#stock'");
				return;
			}
		}
		// 为股票买卖页面添加判断标志位 by ypy
		if (lvl == 2) {
			if ("PP12001".equals(menu.getMenuId())) {
				bf.append("data-tab='stock' href='#");
			} else {
				bf.append("data-tab='tab_ansy' href='#");
			}
		} else {
			bf.append("data-tab='tab_ansy' href='#");
		}

		bf.append(menu.getHref()).append("'");
		bf.append(" data-pid='").append(menu.getMenuId()).append("'");
		bf.append(" data-tabname='").append(menu.getMenuName(language))
				.append("'");
		bf.append(" lvl='").append(lvl).append("'");
	}

	private void gerneryMenuChildren(IMenuItem menu, String language, int lvl,
			StringBuffer bf) {
		List<MenuItem> children = menu.getChildren();
		if (children == null || children.isEmpty())
			return;
		bf.append("<ul>");
		for (MenuItem child : children) {
			generyMenuHtml(bf, child, lvl + 1, language);
		}
		bf.append("</ul>");
	}

	@Override
	public String getMenuHtml(boolean openUser, String language) {
		if (logger.isDebugEnabled()) {
			logger.debug("language----" + language);
			logger.debug("menuCaches----" + menuCaches.toString());
		}
		String key = getMenuKey(language, openUser);
		logger.info("menu key: " + key);
		return menuCaches.get(key);
	}
}
