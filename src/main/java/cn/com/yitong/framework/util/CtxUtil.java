package cn.com.yitong.framework.util;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IParamCover;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public class CtxUtil {

	public static final String TRANS_CODE_KEY = CtxUtil.class.getSimpleName() + "_TRANS_CODE";
	public static final String BUSINESS_CONTEXT_KEY = CtxUtil.class.getSimpleName() + "_BUSINESS_CONTEXT";

	/**
	 * 是否为静态数据交易
	 */
	public static boolean debugTrans(String transCode) {
		return AppConstants.TRANSCODE_USE_STATIC_DATA.contains(transCode);
	}

	public static String transFullPath(String transCode) {
		int len = transCode.length();
		String[] datas = transCode.split("\\/");
		String folder = "";
		if (len > 5) {
			int index = transCode.length() - 5;
			String number = transCode.substring(index);
			if (StringUtil.isNumber(number)) {
				folder = transCode.substring(index, index + 2);
			} else if (transCode.startsWith("crud")) {
				folder = "core";
			}
		}
		StringBuffer bf = new StringBuffer();
		for (int i = 0, j = datas.length; i < j; i++) {
			bf.append("/");
			if (i == (j - 1)) {
				bf.append(folder).append("/").append(datas[i]);
			} else {
				bf.append(datas[i]);
			}
		}
		return bf.toString();
	}

	public static String getDefaultValue(IBusinessContext ctx, MBTransItem item) {
		String defVal = item.getDefaultValue();
		if (defVal.startsWith("@")) {
			String temp = ctx.getSessionText(defVal.substring(1));
			return StringUtil.isEmpty(temp) ? "" : temp;
		}
		return "";
	}

	/**
	 * 创建MAP上下文
	 */
	public static IBusinessContext createMapContext(HttpServletRequest request) {
		return new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
	}

	public static String getStatement(IEBankConfParser confParser, String transCode) {
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		return conf.getProperty(NS.IBATIS_STATEMENT);
	}

	/**
	 * 事务之前公共处理
	 */
	public static boolean transPrev(IBusinessContext ctx, String transCode, IParamCover paramCover,
			IRequstBuilder requestBuilder, IEBankConfParser confParser, Map rst) {
		// 加载参数
		if(!ctx.initParamCover(paramCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return false;
		}
		// 检查报文定义
		if (requestBuilder.buildSendMessage(ctx, confParser, transCode)) {
			return true;
		}
		// 请求报文检查失败
		MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
		return false;
	}

	/**
	 * 事务这公共处理
	 */
	public static void transAfter(IBusinessContext ctx, String transCode, Map rst, boolean ok, IResponseParser responseParser,
			IEBankConfParser confParser) {
		if (ok) {
			ctx.setErrorInfo(AppConstants.STATUS_OK, AppConstants.MSG_SUCC, transCode);
			ctx.setResponseEntry(rst);
			responseParser.parserResponseData(ctx, confParser, transCode);
		} else {
			String msg = StringUtil.isEmpty(rst.get(AppConstants.MSG))?AppConstants.MSG_FAIL:(String)rst.get(AppConstants.MSG);
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, msg, transCode);
		}
		MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
	}

	/**
	 * 事务这公共处理--出错
	 */
	public static Map transError(IBusinessContext ctx, String transCode, Map rst, String errCode, String errMsg) {
		ctx.setErrorInfo(errCode, errMsg, transCode);
		MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
		return rst;
	}

	/**
	 * 事务这公共处理--出错
	 */
	public static Map transError(IBusinessContext ctx, String transCode, Map rst) {
		transError(ctx, transCode, rst, AppConstants.STATUS_FAIL, AppConstants.MSG_FAIL);
		return rst;
	}
}
