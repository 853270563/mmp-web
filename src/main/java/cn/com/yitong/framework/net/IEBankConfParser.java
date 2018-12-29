package cn.com.yitong.framework.net;

import cn.com.yitong.framework.core.bean.MBTransConfBean;

public interface IEBankConfParser {

	public abstract MBTransConfBean findTransConfById(String id);

}