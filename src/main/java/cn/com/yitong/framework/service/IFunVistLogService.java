package cn.com.yitong.framework.service;

import java.util.List;
import java.util.Map;

import cn.com.yitong.framework.core.vo.FunVistLog;

public interface IFunVistLogService {

	/**
	 * 加载交易日志
	 * 
	 * @param FunVistLog
	 * @return
	 */
	public String save(FunVistLog funVistLog);

	/**
	 * 更新上条记录不
	 * 
	 * @param funVistLog
	 */
	public void updateOutTime(FunVistLog funVistLog);

	/**
	 * 获取记录序列
	 * 
	 * @return
	 */
	public String getTranSeq();

	/**
	 * 翻页查询
	 * 
	 * @param param
	 * @return
	 */
	public List pageQuery(Map param);

	/**
	 * 翻页计数查询
	 * 
	 * @param param
	 * @return
	 */
	public int pageCount(Map param);

}
