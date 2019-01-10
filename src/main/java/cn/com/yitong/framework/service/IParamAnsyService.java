package cn.com.yitong.framework.service;

import java.util.List;
import java.util.Map;

public interface IParamAnsyService {

	/**
	 * 刷新系统参数
	 */
	public final String RELOAD_CHANEL_PARAM = "RELOAD_CHANEL_PARAM";
	public final String RELOAD_ERR_INFO = "RELOAD_ERR_INFO";
	public final String RELOAD_LAGG = "RELOAD_LAGG";
	public final String RELOAD_WARM_TIPS = "RELOAD_WARM_TIPS";
	
	public String loadSeq();

	/**
	 * 增加命令
	 * 
	 * @param param
	 * @return
	 */
	public boolean insertCommond(Map param);

	/**
	 * 修改命令状态
	 * 
	 * @param param
	 * @return
	 */
	public boolean updateCommondStatus(Map param);

	/**
	 * 加载命令
	 * 
	 * @param param
	 * @return
	 */
	public Map loadCommond(Map param);

	/**
	 * 查询命令执行情况
	 * 
	 * @param param
	 * @return
	 */
	public List findCommondLog(Map param);

	/**
	 * 查询命令执行日志情况
	 * 
	 * @param param
	 * @return
	 */
	public List findExeLog(Map param);

	/**
	 * 命令执行情况
	 * 
	 * @param param
	 * @return
	 */
	public boolean saveCommRunLog(Map param);
}