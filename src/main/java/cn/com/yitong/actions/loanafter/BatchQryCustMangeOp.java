package cn.com.yitong.actions.loanafter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.dao.IbatisDao;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.ares.flow.IFlowTool;
import cn.com.yitong.ares.flow.plugin.IFlowEmbedOp;
import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 循环调用网贷接口
 * 
 * @author yaoyimin
 *
 */
@Component
public class BatchQryCustMangeOp implements IAresSerivce,IFlowEmbedOp {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IFlowTool flowTool;
	
	@Autowired
	protected IbatisDao ibatisDao;

	/**
	 * @param *flowPath 相对路径
	 */
	@Override
	public int execute(IBusinessContext ctx) {
		String embedFlowPath = ctx.getParam("*flowPath");// 交易前公共处理
		String isPermitted = ctx.getParam("isPermitted");
		String transCode = ctx.getParam("transCode");
		String reportlistname = ctx.getParam("*REPORTLIST");
		String loanstartdate = ctx.getParam("LOAN_START_DATE");
		String loanenddate = ctx.getParam("LOAN_END_DATE");
		
		List<Map> reportlist = new ArrayList<Map>();
		List<String> queryForList = ibatisDao.queryForList("direct.queryuserbydirect", ctx.getParam("WORK_NUMBER"));
		if (queryForList != null) {
			for (int i = 0,j=queryForList.size(); i < j; i++) {
				ctx.setParam("WORK_NUMBER", queryForList.get(i));
				ctx.setParam("isPermitted", isPermitted);
				ctx.setParam("transCode", transCode);
				ctx.setParam("LOAN_START_DATE", loanstartdate);
				ctx.setParam("LOAN_END_DATE", loanenddate);
				logger.debug(ctx.getParamMap().toString());
				flowTool.execute(ctx, embedFlowPath);
				
				List<Map> reportlist2  = (List<Map>) ctx.getParamMap().get(reportlistname);
				for (int j2 = 0, k2=reportlist2.size(); j2 < k2; j2++) {
					Map map = reportlist2.get(j2);
					if (map.size()!=0) {
						reportlist.add(map);
					}
				}
//			ctx.getParamMap().put(reportlistname, reportlist);
			}
		}
		ctx.getParamMap().put(reportlistname, reportlist);
		return NEXT;
	}
}