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
 * @date   2018年8月30日
 */
/**
*填充数据
*@author
*/
@Service
public class Insert2Op implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-填充数据-run--");
		ctx.setParam("UPDATE_DATE", new Date(System.currentTimeMillis()));
		ctx.setParam("CREATE_DATE", new Timestamp(System.currentTimeMillis()));
		ctx.setParam("AUDIT_TIME", new Date(System.currentTimeMillis()));
		ctx.setParam("ID", StringUtil.uuid());
		Map attribute = (Map) SecurityUtils.getSession().getAttribute(SessConsts.USER_INFO);
		ctx.setParam("AUDIT_USER", attribute == null ? null : attribute.get("NAME"));//审批姓名
		return NEXT;
	}

}
