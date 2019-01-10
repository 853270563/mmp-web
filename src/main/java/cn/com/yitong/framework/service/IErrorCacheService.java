package cn.com.yitong.framework.service;


import java.util.List;
import java.util.Map;

public interface IErrorCacheService {
	/**
	 * 加载P_ERROR_CODE错误码 
	 * @param paramMap
	 * @return
	 */
	public List findAllErrCode(Map paramMap);
	/**
	 * 加载P_ERROR_MAPPING错误码
	 * @param paramMap
	 * @return
	 */
	public List findAllErrMapping(Map paramMap);

}
