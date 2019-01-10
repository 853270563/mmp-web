package cn.com.yitong.framework.net;

import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 交易日志保存
 * 
 * @author yaoym
 * 
 */
public interface ITransationLogBuilder {

	public boolean saveTransLog(IBusinessContext busiCtx, String transCode);

}
