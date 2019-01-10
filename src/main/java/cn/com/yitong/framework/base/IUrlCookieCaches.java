package cn.com.yitong.framework.base;

public interface IUrlCookieCaches {

	public abstract void putCookie(String sid, Object client);

	public abstract Object getCookieBySid(String sid);

	public abstract void removeCookie(String sid);

	public abstract boolean hasCookie(String sid);

	public abstract int getSize();

}