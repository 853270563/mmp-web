package cn.com.yitong.util;

import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.vo.TransLogBean;


public class NormalLog {
	public static void initLogParams(IBusinessContext busiCtx, String tranCode, String ACCT_NO){
		// 网银参数
		String cifNo = busiCtx.getSessionText(SessConsts.CIF_NO);
		// 核心客户号
		TransLogBean logBean = busiCtx.getTransLogBean(tranCode);
		logBean.setCifNo(cifNo);
		// 网银登录号
		String loginId = busiCtx.getSessionText(SessConsts.LOGIN_ID);
		logBean.setIbsLgnId(loginId);
		// 交易日期
		String tranDate = DateUtil.todayStr();
		logBean.setPropery("TRAN_DATE", tranDate);
		// 交易时间
		String tranTime = DateUtil.curTimeStr();
		logBean.setPropery("TRAN_TIME", tranTime);
		// 涉及账户
		logBean.setPropery("PAY_ACCT_NO", ACCT_NO);
	}
}