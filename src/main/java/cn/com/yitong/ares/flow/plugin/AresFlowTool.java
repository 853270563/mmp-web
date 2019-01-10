package cn.com.yitong.ares.flow.plugin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.core.AresApp;
import cn.com.yitong.ares.flow.IAresFlowDispatch;
import cn.com.yitong.ares.flow.IFlowTool;
import cn.com.yitong.framework.base.IBusinessContext;

@Component
public class AresFlowTool implements IFlowTool {

	@Value("${FLOW_ROOT_PATH}")
	private String flowRootPath;

	@Override
	public void execute(IBusinessContext ctx, String flwName) {
		IAresFlowDispatch flow = AresApp.getInstance().getFlow(flwName, flowRootPath);
		flow.execute(ctx);
	}

}
