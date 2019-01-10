package cn.com.yitong.framework.net;

import cn.com.yitong.framework.core.bean.MBTransConfBean;

/**
 * 报文定义缓存
 * 
 * @author yaoym
 * 
 */
public interface IEBankConfCaches {

	public abstract void addTransConf(String transCode, MBTransConfBean trans);

	public abstract MBTransConfBean getTransConfById(String transCode);

	public abstract boolean hasTransConfById(String transCode);

	public abstract void removeCache(String key);

}