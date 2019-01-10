package cn.com.yitong.ares.flow;

/**
 * 流程定义加载器
 * 
 * @author yaoym
 * 
 */
public interface IAresFlowDefine {

	/**
	 * 加载流程定义
	 * 
	 * @param define
	 * @param flow
	 * @return
	 */
	public abstract boolean parserDefine(String define, IAresFlowDispatch flow);

}