package cn.com.yitong.framework.service.impl;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.vo.TransLogBean;
import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.service.ICommonService;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.TransSeqNoUtils;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class CommonService implements ICommonService {

	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

	@Override
	public boolean generyTransLogSeq(IBusinessContext ctx, String transCode) {
		TransLogBean lb = ctx.getTransLogBean(transCode);
		lb.setSaved(false);
		lb.setStartTime(new Date().getTime());
		// 设置序列号
		lb.setTransSeqNo(TransSeqNoUtils.getSeqNoUtilsByDate(8));
		// 会话编号
		lb.setSessionId(SecurityUtils.getSessionRequired().getId());
		// 用户号
		Object obj = SecurityUtils.getSessionRequired().getAttribute(NS.LOGIN_ID);
		lb.setLgnId(null==obj?"":obj.toString());
		// 设备
		lb.setDeviceId(SecurityUtils.getSessionRequired().getDeviceCode());
		return true;
	}

	@Override
	public boolean saveJsonTransLog(IBusinessContext ctx, String transCode, Map<String, Object> rst) {
		/*try {
			// 只提取交易号
			TransLogBean logBean = ctx.getTransLogBean(transCode);
			if (logger.isDebugEnabled()) {
				logger.debug("logBean TransCode:" + logBean.getTransCode());
			}

			logBean.setEndTime(new Date().getTime());
			long times = logBean.getEndTime() - logBean.getStartTime();
			if (logger.isInfoEnabled()) {
				logger.info("TransCode:" + transCode + "  交易总共耗时：" + (times) + "(ms)");
			}
			logBean.setTransCost(times + "");

			if (!logBean.isSaved()) {
				logBean.setSaved(true);
				logBean.setRspStatus(null==rst.get(AppConstants.STATUS)?"0":(String)rst.get(AppConstants.STATUS));
				String msg = null==rst.get(AppConstants.MSG)?"":(String)rst.get(AppConstants.MSG);
				if (StringUtil.isNotEmpty(msg)) {
					if (msg.length() > 200) {
						msg = msg.substring(0, 200);
					}
				}
				logBean.setRspMsg(msg);

				// 加载请求参数
				Map<String, String> params = logBean.getProperties();
				dao.insert("TRANS_LOG.savePubTransLog", params);
				if (logger.isInfoEnabled()) {
					logger.info("TransCode:" + transCode + "  交易日志保存成功");
				}
			}
		}catch (Exception e) {
			logger.error("共用日志保存失败：", e);
			return false;
		}*/
		return true;
	}
}
