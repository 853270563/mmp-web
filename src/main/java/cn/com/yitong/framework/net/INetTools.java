package cn.com.yitong.framework.net;

import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 可切换的通讯组件接口
 * 
 * @author yaoym
 * 
 */
public interface INetTools {

	/**
	 * @param busiCtx
	 * @param transCode
	 * @return
	 */
	boolean execute(IBusinessContext busiCtx, String transCode);

}
