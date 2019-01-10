package cn.com.yitong.framework.net.impl.push;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.SmsProperties;
import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.net.INetTools;
import cn.com.yitong.framework.service.ICommonService;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

/**
 * @author yaoym
 * 
 */
@Service
public class SmsServcice {
	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	@Qualifier("netTools4push")
	protected INetTools netTools;
	@Autowired
	protected ICommonService commonService;

	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

	private final String NS_MOBILE_NO = "S_MOBILEN_UM";
	private final String NS_SMS_MSG = "S_SMSMSG";
	private final String NS_SMS_LANG = "S_SMS_LANG";
	private final String NS_PRIORITY = "IPRIORITY";

	/**
	 * 发送短信
	 * 
	 * @param busiCtx
	 * @param transCode
	 * @return
	 */
	public boolean sendSms(IBusinessContext busiCtx, String transCode) {
		boolean sms = SmsProperties.getBoolean(transCode);
		if (!sms) {
			return true;
		}
		logger.info("发送交易变更短信通知:" + transCode);
		// 手机号码
		// String mobileNo = busiCtx.getSessionText(SessConsts.MOBILE_NO);
		Map paramMap = new HashMap();
		// 首先判断是否开通短信提醒
		paramMap.put("CIF_NO", busiCtx.getSessionText(SessConsts.CIF_NO));
		if (findServOpenByCif(paramMap, "PP11010")
				&& findServOpenByCif(paramMap, transCode)) {
			logger.info("已经开通了短信提醒服务:" + transCode);
		} else { 
			return false;
		}
		// 从数据库获取手机号
		Map Usermobile = (Map) dao.queryForMap("P_USER.findPersonMobile", paramMap);
		String mobileNo = String.valueOf(Usermobile.get("SECOND_MB_NO")); 
		String msg = "";
		if (AppConstants.STATUS_OK.equals(busiCtx.getResponseStatus())) {
			msg = SmsProperties.getUtf8(transCode + "_T_MSG");
		} else {
			msg = SmsProperties.getUtf8(transCode + "_F_MSG");
		}
		// System.out.print("msg===" + msg);
		if (StringUtil.isEmpty(mobileNo) || StringUtil.isEmpty(msg)) {
			logger.warn("参数未定义,不能发送短信!");
			return true;
		}
		return sendMessage(busiCtx, transCode, mobileNo, msg);
	}

	/**
	 * 短信发送
	 * 
	 * @param busiCtx
	 * @param transCode
	 * @param mobileNo
	 * @param msg
	 * @return
	 */
	public boolean sendMessage(IBusinessContext busiCtx, String transCode,
			String mobileNo, String msg) {
		busiCtx.setParam(NS_MOBILE_NO, mobileNo);
		// 短信内容
		busiCtx.setParam(NS_SMS_MSG, msg);
		// 短信语言
		String language = busiCtx.getSessionText(SessConsts.LANGUAGE);
		busiCtx.setParam(NS_SMS_LANG, getSmsLanguage(language));

		busiCtx.setParam(NS_PRIORITY, "0");// 0：立即發送 1：排隊發送

		String pushSerivce = "SendSMS";
		// 生成序列号
		commonService.generyTransLogSeq(busiCtx, pushSerivce);
		if (netTools == null) {
			// 通讯组件加载未设置
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL,
					"SMS NetTool通讯组件未设置！", transCode);
			return false;
		}
		return netTools.execute(busiCtx, pushSerivce);
	}

	/**
	 * 短信语言
	 * 
	 * @param language
	 * @return
	 */
	private String getSmsLanguage(String language) {
		if (AppConstants.EN.equals(language)) {
			return "E";
		} else {
			return "C";
		}
	}

	/**
	 * 判断该用户是否开通短信提醒 判断该用户是否开通功能对应的提醒
	 * 
	 * @param paramMap
	 * @return
	 */
	private boolean findServOpenByCif(Map paramMap, String Code) {
		// 短信提醒服务
		paramMap.put("SRV_NO", Code);
		if (dao.queryForMap("SERV_OPEN.findByCifSmsServ", paramMap) == null) {
			return false;
		}
		return true;
	}
}
