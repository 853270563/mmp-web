package cn.com.yitong.actions.apply;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.StringUtil;

/**
 * @author luanyu
 * @date   2018年8月29日
 */
/**
*填充数据
*@author
*/
@Service
public class InsertOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-填充数据-run--");
		ctx.setParam("APPLY_TIME", new Timestamp(System.currentTimeMillis()));
		ctx.setParam("START_TIME", new Timestamp(System.currentTimeMillis()));
		ctx.setParam("UPDATE_DATE", new Date(System.currentTimeMillis()));
		ctx.setParam("CREATE_DATE", new Timestamp(System.currentTimeMillis()));
		ctx.setParam("APPLY_ID", StringUtil.uuid());
		ctx.setParam("AUDIT_TIME", new Date(System.currentTimeMillis()));
		ctx.setParam("PROCESS_INST_ID", StringUtil.uuid());
		ctx.setParam("ID", StringUtil.uuid());
		ctx.setParam("ACT_TYPE", "1");//  '1：发起环节2：审批环节 3：发起人撤销';
		Map attribute = (Map) SecurityUtils.getSession().getAttribute(SessConsts.USER_INFO);
		if (StringUtil.isEmpty(ctx.getParam("CUSTOMER_MANAGER_NAME"))) {

			ctx.setParam("AUDIT_USER", attribute == null ? null : attribute.get("NAME"));//审批姓名
		} else {
			ctx.setParam("AUDIT_USER", ctx.getParam("CUSTOMER_MANAGER_NAME"));//审批姓名

		}
		return NEXT;
	}

}
