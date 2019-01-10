package cn.com.yitong.framework.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

import cn.com.yitong.ares.consts.AresR;
import cn.com.yitong.ares.core.AresApp;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.error.OtherRuntimeException;
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

public class CtxUtil {

	public static final String TRANS_CODE_KEY = CtxUtil.class.getSimpleName() + "_TRANS_CODE";
	public static final String BUSINESS_CONTEXT_KEY = CtxUtil.class.getSimpleName() + "_BUSINESS_CONTEXT";

	/**
	 * 是否为静态数据交易
	 * 
	 * @param transCode
	 * @return
	 */
	public static boolean debugTrans(String transCode) {
		return AppConstants.TRANSCODE_USE_STATIC_DATA.contains(transCode);
	}

	/**
	 * 交易命名规范,追加目录 <br>
	 * 交易码后五位为数字，1-2位为目录，3-5位为序号，分离出目录：<br>
	 * 交易码后五位为为字母，统一追加 core目录：<br>
	 * 举例：<br>
	 * crud/ItemList==>crud/core/ItemList<br>
	 * crud/PP01010==>crud/01/PP01010<br>
	 * login/ClientLogin==>login/ClientLogin<br>
	 * 
	 * @param transCode
	 * @return
	 */
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

	/**
	 * 缺省值： <br>
	 * “@”开头为Session中取值 <br>
	 * “$”开头为日期<br>
	 * “#”上下文中取值
	 * 
	 * @param ctx
	 * @param item
	 * @return
	 */
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
	 * 
	 * @param request
	 * @return
	 */
	public static IBusinessContext createMapContext(HttpServletRequest request) {
		return new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
	}

	/**
	 * 
	 * @param confParser
	 * @param transCode
	 * @return
	 */
	public static String getStatement(IEBankConfParser confParser,
			String transCode) {
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		return conf.getProperty(NS.IBATIS_STATEMENT);
	}

	/**
	 * 事务之前公共处理
	 * 
	 * @param ctx
	 * @param transCode
	 * @param paramCover
	 * @param requestBuilder
	 * @param confParser
	 * @param rst
	 * @return
	 */
	public static boolean transPrev(IBusinessContext ctx, String transCode,
			IParamCover paramCover, IRequstBuilder requestBuilder,
			IEBankConfParser confParser, Map rst) {
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
	 * 
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	public static void transAfter(IBusinessContext ctx, String transCode,
			Map rst, boolean ok, IResponseParser responseParser,
			IEBankConfParser confParser) {
		if (ok) {
			ctx.setSuccessInfo(AppConstants.STATUS_OK, AppConstants.MSG_SUCC,
					transCode);
			ctx.setResponseEntry(rst);
			if (responseParser.parserResponseData(ctx, confParser, transCode)) {
				return;
			}
		}
		MessageTools.elementToMap(ctx.getResponseContext(), rst);

	}

	/**
	 * 事务这公共处理--出错
	 * 
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	public static Map transError(IBusinessContext ctx, String transCode,
			Map rst, String errCode, String errMsg) {
		ctx.setErrorInfo(errCode, errMsg, transCode);
		MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
		return rst;
	}

	/**
	 * 事务这公共处理--出错
	 * 
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	public static Map transError(IBusinessContext ctx, String transCode, Map rst) {
		transError(ctx, transCode, rst, AppConstants.STATUS_FAIL,
				AppConstants.MSG_FAIL);
		return rst;
	}
	/**
	 * 替换输入模板中的参数,参数格式{变量}
	 */
	public static String makesoapRequestData(Map<String, String> params,
			String template) throws Exception {
		// 替换入参模版中的占位符
		String matchStr = "\\{\\w+}";
		Pattern localPattern = Pattern.compile(matchStr);
		Matcher localMatcher = localPattern.matcher(template);
		while (localMatcher.find()) {
			int i = localMatcher.start();
			int j = localMatcher.end();
			// 原字符串拆分
			String paramName = template.substring(i + 1, j - 1);
			String replaceStr = "\\{" + paramName + "\\}";
			// 内存中哈希Map
			String retValue = String.valueOf(params.get(paramName));
			// 赋值（如果内存中没有该值，赋为""）
			if (!StringUtils.hasText(retValue) || retValue.equals("null")) {
				retValue = "";
			}
			template = template.replaceAll(replaceStr, retValue);
			System.out.println(replaceStr + "=" + retValue);
			localMatcher = localPattern.matcher(template);
		}
		return template;
	}

	public static Map<String, Object> showErrorResult(Exception e, IBusinessContext ctx) {
		if (e instanceof AresRuntimeException) {
			return showAresError((AresRuntimeException) e, ctx);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		String error = "common.system.error";
		String msg = AresApp.getInstance().getMessage(error, AresR.EMPTY_PARAMS);
		map.put(AresR.RTN_CODE, error);
		map.put(AresR.RTN_MSG, msg);
		return map;
	}



	private static Map<String, Object> showAresError(AresRuntimeException e, IBusinessContext ctx) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (e instanceof OtherRuntimeException) {
			OtherRuntimeException other = (OtherRuntimeException) e;
			map.put(AresR.RTN_CODE, other.getErrorCode());
			map.put(AresR.RTN_MSG, other.getErrorMessage());
		} else {
			String msg = AresApp.getInstance().getMessage(e.getMessageKey(), e.getArgs());
			map.put(AresR.RTN_CODE, e.getMessageKey());
			map.put(AresR.RTN_MSG, msg);
		}
		return map;
	}

	/**
	 * 简单成功信息返回
	 * 
	 * @param ctx
	 * @return
	 */
	public static Map<String, Object> showSuccessResult(Map<String, Object> outMap) {
		String msg = AresApp.getInstance().getMessage(AresR.RTN_SUCCESS, AresR.EMPTY_PARAMS);
		outMap.put(AresR.RTN_CODE, AresR.RTN_SUCCESS);
		outMap.put(AresR.RTN_MSG, msg);
		return outMap;
	}
}
