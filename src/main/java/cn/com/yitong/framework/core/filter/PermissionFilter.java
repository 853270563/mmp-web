package cn.com.yitong.framework.core.filter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IBusinessFilter;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

/**
 * 交易限检查实现类
 * 
 * @author yaoym
 * 
 */
@Component
public class PermissionFilter implements IBusinessFilter {
	private Logger logger = YTLog.getLogger(this.getClass());
	/**
	 * 功能受限的交易清單
	 */
	private final String FORBID_TRANS_CODES = "PD04022Op|PD04023Op|PD04032Op|PD04033Op|PD04042Op|PD04043Op|BO01000Op|PD04302Op|PD04303Op";

	public boolean validate(IBusinessContext ctx, String transCode) {
		String chanel = ctx.getChanelType();
		boolean withoutSession = StringUtil.isNotEmpty(chanel);
		if (withoutSession) {
			return true;
		}		
		// 静态密码登陆的用户不能做交易过滤
		String authType = ctx.getSessionText(SessConsts.AUTH_TYPE);
		//如果是二维码转账或手机号转账，普通方式可以操作；
		String TRANSF_FLG=ctx.getParam("TRANSF_FLG");
		if (NS.AUTH_TYP_NULL.equals(authType)) {
			if (FORBID_TRANS_CODES.indexOf(transCode) >= 0) {
				if(!"4".equals(TRANSF_FLG)&&!"5".equals(TRANSF_FLG)){
					ctx.setErrorInfo(AppConstants.STATUS_FAIL, "請用戶以短訊密碼或安全密碼方式登錄操作!",//"该交易您未获得授权!",
							transCode);
					return false;
				}
			}
		}
		// 账号有效性检查
		if (FORBID_ACCTVALID_CODES.indexOf(transCode) < 0
				&& !validateAccount(ctx, transCode)&&!"5".equals(TRANSF_FLG)) {
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "该账户您未获得授权!", transCode);
			return false;
		}
		return true;
	}

	/**
	 * 賬號合法性檢查交易清單
	 */
	private final String FORBID_ACCTVALID_CODES = "PD04062Op|PD04066Op|CP015Op|PP12026|CP050Op|CP004Op|CP051Op|PP05009Op";

	/**
	 * 账号有效性检查
	 * 
	 * @param ctx
	 * @param transCode
	 * @return
	 */
	private boolean validateAccount(IBusinessContext ctx, String transCode) {
		// 账号有效性检查
		String acctNo = ctx.getParam(NS.ACCT_NO);
		if (StringUtil.isEmpty(acctNo)) {
			acctNo = ctx.getParam(NS.PAY_ACCT_NO);
		}
		if (StringUtil.isEmpty(acctNo)) {
			return true;
		}
		try {
			Map<String, List> acctMap = (Map) ctx.getSessionObject(SessConsts.ACCT_MAP);
			if (acctMap == null || acctMap.isEmpty()) {
				logger.debug("用户未加载下挂账户");
				return false;
			}
			Collection<List> accts = acctMap.values();
			for (List<Map> list : accts) {
				for (Map map : list) {
					if (acctNo.equals(map.get(NS.ACCT_NO))) {
						if (logger.isDebugEnabled()) {
							logger.debug("validateAccount:" + acctNo);
						}
						return true;
					}
				}
			}
		} catch (Exception e) {
			logger.error("--validateAccount---:\n", e);
			return true;
		}
		return false;
	}
}
