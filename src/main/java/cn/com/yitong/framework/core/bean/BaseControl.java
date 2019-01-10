package cn.com.yitong.framework.core.bean;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.ExceptionHandler;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.vo.FunVistLog;
import cn.com.yitong.framework.net.IBusinessFilter;
import cn.com.yitong.framework.net.INetTools;
import cn.com.yitong.framework.net.IParamCover;
import cn.com.yitong.framework.net.impl.push.SmsServcice;
import cn.com.yitong.framework.service.ICommonService;
import cn.com.yitong.framework.service.IFunVistLogService;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

import cn.com.yitong.core.base.WebUtils;

/**
 * 基础控制类
 * <p>
 * 定义交易共享的方法放置基中
 * </p>
 * 
 * @author yaoym
 * 
 */
public abstract class BaseControl {
	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	@Qualifier("netTools4btt")
	protected INetTools netTools;

	@Autowired
	protected BusinessContextFactory busiCtxFactory;
	@Autowired
	@Qualifier("permissionFilter")
	protected IBusinessFilter permissionFilter;
	@Autowired
	@Qualifier("jsonParamCover")
	protected IParamCover jsonParamCover;
	@Autowired
	@Qualifier("json2MapParamCover")
	protected IParamCover json2MapParamCover;
	@Autowired
	protected ICommonService commonService;
	@Autowired
	protected SmsServcice smsService;
	protected final boolean DEBUG = false;

	/**
	 * 基本交易处理
	 * 
	 * @param request
	 * @return
	 */
	public boolean execute(IBusinessContext busiCtx,
			HttpServletRequest request, String transCode) {
		return this.execute(busiCtx, request, transCode, netTools);
	}

	/**
	 * 基本交易处理
	 * 
	 * @param request
	 * @return
	 */
	public boolean execute(IBusinessContext busiCtx,
			HttpServletRequest request, String transCode, INetTools netTools) {
		// 参数检查
		if (StringUtil.isEmpty(transCode)) {
			transCode = "transCode";
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "缺少必要参数!", transCode);
			return false;
		}
		// 单次会话检查
		if (!checkSingleSequnce(busiCtx, transCode)) {
			return false;
		}
		// 生成序列号
		commonService.generyTransLogSeq(busiCtx, transCode);
		// 交易权限检查
		if (!permissionFilter.validate(busiCtx, transCode)) {
			return false;
		}
		if (netTools == null) {
			// 通讯组件加载未设置
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "NetTool通讯组件未设置！",
					transCode);
			return false;
		}
		boolean rst = netTools.execute(busiCtx, transCode);
		// 发送短信
		smsService.sendSms(busiCtx, transCode);
		// 保存交易日志,此日志不知有何用,暂时去除(2015-10-23 modify by zhuzengpeng)
		//commonService.saveTransLog(busiCtx, transCode);
		return rst;
	}

	// 强制单次检查的交易清单
	private final String TRANS_SIGNLE_CHECK = AppConstants.TRANS_SIGNLE_CHECK;

	/**
	 * 单次检查设置
	 * 
	 * @param busiCtx
	 * @param transCode
	 * @return
	 */
	public boolean checkSingleSequnce(IBusinessContext busiCtx, String transCode) {
		if (!TRANS_SIGNLE_CHECK.contains(transCode)) {
			return true;
		}
		String sessCode = busiCtx.getSessionText(SessConsts.SIGNLE_SEQ);
		if (StringUtil.isEmpty(sessCode)) {
			// 无效请求，前端不作处理
			busiCtx.setErrorInfo(AppConstants.STATUS_RESUBMIT,
					AppConstants.MSG_FAIL, transCode);
			return false;
		}
		busiCtx.removeSession(SessConsts.SIGNLE_SEQ);// 去除一次性会话；
		// 获取 请求验证码
		String checkCode = busiCtx.getParam(NS.SIGNLE_SEQ);
		if (!sessCode.equals(checkCode)) {
			busiCtx.setErrorInfo(AppConstants.STATUS_RANDOM_ERROR,
					AppConstants.MSG_RANDOM_ERROR, transCode);
			return false;
		}
		return true;
	}

	/**
	 * 设置交易失败信息
	 * 
	 * @param rst
	 * @param status
	 * @param msg
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void responseFailure(Map rst, String status, String msg) {
		rst.put(AppConstants.STATUS, status);
		rst.put(AppConstants.MSG, msg);
	}

	/**
	 * 设置交易成功信息
	 * 
	 * @param rst
	 * @param msg
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void responseSuccess(Map rst, String msg) {
		rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
		rst.put(AppConstants.MSG, msg);
	}

	/**
	 * 获取多语言路径前缀
	 * 
	 * @param busiCtx
	 * @return
	 */
	protected String getPrexPath(IBusinessContext busiCtx) {
		String prex = "";
		String language = busiCtx.getSessionText(SessConsts.LANGUAGE);
		if (AppConstants.ZH_CN.equals(language)) {
			prex = "auto_zh/";
		} else if (AppConstants.EN.equals(language)) {
			prex = "auto_en/";
		}
		return prex;
	}

	@Autowired
	protected IFunVistLogService funVistLogService;

	/**
	 * 保存功能访问日志
	 * 
	 * @param ctx
	 * @param transCode
	 * @return
	 */
	protected boolean saveFunVistLog(IBusinessContext ctx, String transCode) {
		FunVistLog funVistLog = null;
		String lastedTranSeq = ctx.getSessionText(SessConsts.FUN_VIST_LOG_SEQ);
		if (null != lastedTranSeq) {
			// 设置上次功能访问离开时间
			funVistLog = new FunVistLog();
			funVistLog.setTranSeq(lastedTranSeq);
			funVistLog.setOutTime(DateUtil.curTimeStr());
			funVistLog.setInfoStat("1");// 1：有效状态
			funVistLogService.updateOutTime(funVistLog);
		}
		// 获取功能访问日志序列
		funVistLog = new FunVistLog();
		funVistLog.setInfoStat("0"); // 0：初始状态
		funVistLog.setClientIp(ctx.getSessionText(SessConsts.CLIENT_IP));
		funVistLog.setSessId(ctx.getHttpSession().getId());
		funVistLog.setUserSessSeq(ctx.getSessionText(SessConsts.SESS_SEQ));
		funVistLog.setCifNo(ctx.getSessionText(SessConsts.CIF_NO));
		funVistLog.setVistMenuId(transCode);
		funVistLog.setTranDate(DateUtil.todayStr());
		funVistLog.setInTime(DateUtil.curTimeStr());
		// 保存当前功能访问记录
		String tranSeq = funVistLogService.save(funVistLog);
		// 保存本次功能访问日志序列
		ctx.saveSessionText(SessConsts.FUN_VIST_LOG_SEQ, tranSeq);
		return true;
	}

    @ExceptionHandler({Exception.class})
    public void exceptionHandler(HttpServletRequest request, HttpServletResponse response,
                                 Exception e) {
        WebUtils.jsonExceptionHandler(response, e);
    }
}
