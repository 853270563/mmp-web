package cn.com.yitong.framework.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.consts.NS;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.vo.TransLogBean;
import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.net.impl.esb.ESBConst;
import cn.com.yitong.framework.service.ICommonService;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

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
		lb.setPropery(NS.TRANS_FLG, lb.getTransFlag(transCode));
		lb.setStartTime(new Date().getTime());
		// 设置序列号
		int seq = Integer.valueOf(String.valueOf(dao.load("SEQ.P_TRANS_LOGS_SEQ", null)));
		int iSeq = seq % 100000;
		lb.setTransSeqNo(DateUtil.todayStr("yyyyMMddHHmmss")
				+ StringUtil.lpadString("" + iSeq, 6, "0"));
		// 会话编号
		lb.setPropery(NS.SESS_SEQ, ctx.getSessionText(SessConsts.SESS_SEQ));
		lb.setPropery(NS.USER_SESS_ID, ctx.getHttpSession().getId());
		// 用户号
		lb.setPropery(SessConsts.LOGIN_ID,
				ctx.getSessionText(SessConsts.LOGIN_ID));
		return true;
	}

	@Override
	public boolean saveTransLog(IBusinessContext busiCtx, String transCode) {
		TransLogBean logBean = busiCtx.getTransLogBean(transCode);

		logBean.setEndTime(new Date().getTime());
		long times = logBean.getStartTime() - logBean.getEndTime();
		if (logger.isInfoEnabled()) {
			logger.info("TransCode:" + transCode + "  交易总共耗时" + (times)
					+ "(ms)");
		}
		if (logBean.isNeedSaveLog()) {
			// 交易号-功能信息-主要操作元素-交易结果
			logBean.setRspCode(busiCtx.getRspCode());
			String msg = busiCtx.getRspMsg();
			if (StringUtil.isNotEmpty(msg)) {
				if (msg.length() > 200)
					msg = msg.substring(0, 200);
			}
			logBean.setRspMsg(msg);
			// 加载请求参数
			Map<String, String> params = logBean.getProperties();
			// 请求参数
			String[] reqs = logBean.getRequestCtxParams();
			logger.info("get value of reqs=" + reqs);
			if (null != reqs && reqs.length > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("TransCode db log request ctx :"
							+ reqs.toString());
				}
				Map tmps = StringUtil.xmlToMap(
						busiCtx.getRequestContext(transCode), reqs);
				params.putAll(tmps);
			}
			// 响应参数
			String[] rsps = logBean.getRsponseCtxParams();
			if (null != rsps && rsps.length > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("TransCode db log response ctx :"
							+ rsps.toString());
				}
				Map tmps = StringUtil.xmlToMap(
						busiCtx.getResponseContext(transCode), rsps);
				params.putAll(tmps);
			}
			// 页面参数
			String[] basies = logBean.getBaseParams();
			if (null != basies && basies.length > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("TransCode db log response ctx :"
							+ basies.toString());
				}
				Map tmps = StringUtil.xmlToMap(busiCtx.getParamContext(),
						basies);
				params.putAll(tmps);
			}
			// 消息模板
			String msgt = logBean.getMessageTemplate();
			if (StringUtil.isNotEmpty(msgt)) {
				// 消息參數
				String[] tmps = logBean.getMessageParams();
				for (int i = 0; i < tmps.length; i++) {
					msgt = msgt.replaceAll("\\{" + i + "\\}", tmps[i]);
				}
				// 消息信息
				logBean.setPropery(NS.LOG_OTH_MSG, msgt);
			}
			String sqlMap = logBean.getSqlMap();

			// 覆盖参数中的CIF_NO 为网银的cifNo
			params.put("CIF_NO", busiCtx.getSessionText(NS.CIF_NO));
			params.put("IBS_LGN_ID", busiCtx.getSessionText(NS.IBS_LGN_ID));
			// 默认非动账

			if (StringUtil.isEmpty(params.get("TRANS_FLG"))) {
				params.put("TRANS_FLG", "2");
			}
			if (StringUtil.isEmpty(sqlMap)) {
				dao.insert("TRANS_LOG.savePubTransLog", params);
			} else {
				dao.insert(logBean.getSqlMap(), params);
			}
			if (logger.isInfoEnabled()) {
				logger.info("TransCode:" + transCode + "  交易日志保存成功");
			}
		}
		return false;
	}

	/**
	 * 
	 * @param ctx
	 * @param transCode
	 * @return
	 */
	@Override
	public boolean saveJsonTransLog(IBusinessContext ctx, String transCode,
			Map<String, Object> rst) {
		// 只提取交易号
		TransLogBean logBean = ctx.getTransLogBean(transCode);
		System.out.println("logBean TransCode:" + logBean.getTransCode());
		if (logBean.isNeedSaveLog() && !logBean.isSaved()) {
			logBean.setSaved(true);
			String sqlMap = logBean.getSqlMap();
			if (StringUtil.isEmpty(sqlMap)) {
				logger.warn("savelog sqlmap undefined, transCode is :"
						+ transCode);
				return false;
			}
			// 交易号-功能信息-主要操作元素-交易结果
			// 加载请求参数
			Map<String, String> params = logBean.getProperties();
			for (String key : rst.keySet()) {
				if (!params.containsKey(key)) {
					Object obj = rst.get(key);
					if (obj instanceof java.util.List) {
						continue;
					}
					params.put(key, StringUtil.getString(rst, key, ""));
				}
			}
			// 消息模板
			String msgt = logBean.getMessageTemplate();
			if (StringUtil.isNotEmpty(msgt)) {
				// 消息參數
				String[] tmps = logBean.getMessageParams();
				for (int i = 0; i < tmps.length; i++) {
					msgt = msgt.replaceAll("\\{" + i + "\\}", tmps[i]);
				}
				// 消息信息
				logBean.setPropery(NS.LOG_OTH_MSG, msgt);
			}
			// 用户
			params.put(SessConsts.LOGIN_ID,
					ctx.getSessionText(SessConsts.LOGIN_ID));
			// 机构
			params.put(SessConsts.ORGAN_ID,
					ctx.getSessionText(SessConsts.ORGAN_ID));
			dao.insert(logBean.getSqlMap(), params);

			if (logger.isInfoEnabled()) {
				logger.info("TransCode:" + transCode + "  交易日志保存成功");
			}
		}
		return true;
	}

	@Override
	public String generyServiceSn() {
		// TODO Auto-generated method stub
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 流水号规则
	 *01-07 7 系统 ID(3 位的成员行 ID+4 位的系统代码)<br>
	08-09 2 ESB 保留。固定为“ 00” 。<br>
	10-10 1 ESB 保留，外围不可使用此位置。 固定为 0。
	ESB 处理组合服务，生成子服务流水号时，会变更此字段。
	0018 系统上送的交易按照 A/B/C 依次递增。其他系统上送的交易设置为当前步骤号。<br>
	11-12 2 流水号自定义段_前段<br>
	13-13 1 固定为 9（表示外围流水号）<br>
	14-19 6 流水号自定义段_后段<br>
	 * 
	 */
	public String generyYtServiceSn() {
		String strSeq = String.valueOf(dao.load("SEQ.ESB_PREFIX", null));
		String strSeq2 = String.valueOf(dao.load("SEQ.ESB_PREFIX2", null));
		strSeq = StringUtil.lpadString(strSeq, 6, "0");
		strSeq2 = StringUtil.lpadString(strSeq2, 2, "0");
		return ESBConst.ESB_SERIVALSN + strSeq2 + 9 + strSeq;
	}

}
