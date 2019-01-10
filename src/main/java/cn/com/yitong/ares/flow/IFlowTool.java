package cn.com.yitong.ares.flow;

import cn.com.yitong.framework.base.IBusinessContext;

public interface IFlowTool {

	/**
	 * 调用Flow流程
	 * 
	 * @param ctx
	 * @param flowPath
	 * @return
	 */
	public void execute(IBusinessContext ctx, String flowPath);

}
