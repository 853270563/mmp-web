package cn.com.yitong.actions.atom;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.dao.IbatisDao;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.common.ValidUtils;

/**
 * @author luanyu
 * @date   2018年9月12日
 */
/**
*权限检查
*@author
*/
@Service
public class PermissionCheckOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected IbatisDao ibatisDao;
	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-权限检查-run--");
		String permission = ValidUtils.validEmpty("*permission", ctx.getParamMap());
		String userId = cn.com.yitong.core.util.SecurityUtils.getSession().getUserId();
		List<String> queryForList = ibatisDao.queryForList("mobileMenu.findByUserId", userId);
//		List<String> queryForList = ibatisDao.queryForList("mobileMenu.findByUserId", ctx.getParam("WORK_NUMBER"));
		boolean isPermitted = false;
		for (String string : queryForList) {
			if (StringUtil.isNotEmpty(string) && string.contains(permission)) {
				isPermitted = true;
				break;
			}
		}
		if (isPermitted) {
			ctx.setParam("isPermitted", "1");
		} else {
			ctx.setParam("isPermitted", "0");
		}

		return NEXT;
	}

}
