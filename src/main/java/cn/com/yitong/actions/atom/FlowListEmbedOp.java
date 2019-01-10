package cn.com.yitong.actions.atom;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.ares.flow.IFlowTool;
import cn.com.yitong.ares.flow.plugin.IFlowEmbedOp;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.common.StringUtil;

/**
 * Flow列表遍历流程，单值遍历，子流程中不包含事务
 * 
 * @author yaoyimin
 *
 */
@Component
public class FlowListEmbedOp implements IAresSerivce, IFlowEmbedOp {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IFlowTool flowTool;

	/**
	 * @param *flowPath 相对路径
	 * @param *listKey 列表数据字段名，缺省为LIST
	 * @param *itemKey 列表子数据字段名，缺省为item
	 * @param *itemIndex 列表子索引字段名，缺省为index
	 */
	@Override
	public int execute(IBusinessContext ctx) {
		String embedFlowPath = ctx.getParam("*flowPath");
		String listKey = ctx.getParam("*listKey", "LIST");
		String itemKey = ctx.getParam("*itemKey", "item");
		String rspKey = ctx.getParam("*rspKey");//*代表所有 ,多个写法：[a][b][c]
		String itemIndex = ctx.getParam("*itemIndex", "index");
		List<Map> datas = ctx.getParamDatas(listKey);
		int index = 0;
		for (Map data : datas) {
			logger.debug("listKey:{}, item index:{}", listKey, index);
			ctx.setParam(itemIndex, index++);
			ctx.setParam(itemKey, data);
			flowTool.execute(ctx, embedFlowPath);
			if(StringUtil.isNotEmpty(rspKey)){
					Set set = 	ctx.getParamMap().keySet();
					Iterator iterator = set.iterator();
					while(iterator.hasNext()){
						String key = (String) iterator.next();
						Object value = ctx.getParam(key);
						if(!(value instanceof List)){
							if("*".equals(rspKey) || rspKey.contains("["+key+"]")){
								data.put(key, value);
							}
						}
					} 
			}
		}
		ctx.removeParam(itemKey);
		ctx.removeParam(itemIndex);

		return NEXT;
	}
}