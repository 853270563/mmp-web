package cn.com.yitong.framework.service;

import java.util.Map;

import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 系统序列
 * 
 * @author yaoym
 * 
 */
public interface ICommonService {

	/**
	 * 交易日志序列，可根据交易种类进行生成不同规则的序列号
	 * 
	 * @return
	 */
	public boolean generyTransLogSeq(IBusinessContext busiCtx, String transCode);

	/**
	 * 保存交易日志
	 * 
	 * @param busiCtx
	 * @return
	 */
	public boolean saveTransLog(IBusinessContext busiCtx, String transCode);

	/**
	 * 保存Json交易日志
	 * 
	 * @param busiCtx
	 * @param transCode
	 * @return
	 */
	public boolean saveJsonTransLog(IBusinessContext busiCtx, String transCode,
									Map<String, Object> rst);

	/**
	 * 交易记录流水号，请求服务的唯一标识
	 * 
	 * @return
	 */
	public String generyServiceSn();

}
