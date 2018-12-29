package cn.com.yitong.framework.service;

import cn.com.yitong.framework.base.IBusinessContext;

import java.util.Map;

/**
 * 系统序列
 * 
 * @author yaoym
 * 
 */
public interface ICommonService {

	/**
	 * 交易日志序列，可根据交易种类进行生成不同规则的序列号
	 */
	public boolean generyTransLogSeq(IBusinessContext busiCtx, String transCode);

	/**
	 * 保存Json交易日志
	 */
	public boolean saveJsonTransLog(IBusinessContext busiCtx, String transCode, Map<String, Object> rst);
}
