package cn.com.yitong.framework.base;

public interface IMenuCaches {

	/**
	 * 菜单网页
	 * 
	 * @param openUser 特定开放用户, 可以显示可测试菜单
	 * @param language 多语言
	 * @return
	 */
	public String getMenuHtml(boolean openUser, String language);

}