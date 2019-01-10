package cn.com.yitong.actions.atom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.ares.flow.IFlowTool;
import cn.com.yitong.ares.flow.plugin.IFlowEmbedOp;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 内嵌Flow子流程，子流程中不包含事务
 * 
 * @author yaoyimin
 *
 */
@Component
public class FlowEmbedOp implements IAresSerivce,IFlowEmbedOp {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IFlowTool flowTool;

	/**
	 * @param *flowPath 相对路径
	 */
	@Override
	public int execute(IBusinessContext ctx) {
		String embedFlowPath = ctx.getParam("*flowPath");// 交易前公共处理
		flowTool.execute(ctx, embedFlowPath);
		return NEXT;
	}
}