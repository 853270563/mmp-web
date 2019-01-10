package cn.com.yitong.framework.core.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IParamCover;
import cn.com.yitong.framework.util.security.SecurityUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Component
public class Json2MapParamCover implements IParamCover {
	private Logger logger = YTLog.getLogger(this.getClass());
//    private List<String> RSA_KEY_LIST = Arrays.asList("USER_PSW");

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean cover(IBusinessContext ctx, HttpServletRequest request,
			boolean signed, String transCode) throws Exception {
		if (null == request || ctx == null)
			return false;

		Map params = ctx.getParamMap();
		// 加载URL中的参数
		parserUrlParams(request, params);

		// 请求原文
		StringBuffer sb = new StringBuffer();
		BufferedReader br;
		try {
			br = request.getReader();
			String str = null;
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
		} catch (IOException e) {
			logger.error("request json str buffer is error!", e);
		}
		String msgstr = sb.toString();
		
		if (StringUtil.isEmpty(msgstr)) {
			return true;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("request jsonstr:\n" + msgstr);
		}
		try {
			if (signed) {
				if (logger.isDebugEnabled()) {
					logger.debug("request jsonstr signed is " + signed);
				}
				// 解码
				msgstr = SecurityUtil.deEncrypt(msgstr);
				if (StringUtil.isEmpty(msgstr)) {
					logger.warn("signed request jsonstr is null");
					return false;
				}
				Map<Object, Object> temps = JsonUtils.jsonToMap(msgstr);
				params.putAll(temps);
				if (logger.isDebugEnabled()) {
					logger.debug("signed request jsonstr is\n" + msgstr);
				}
				// 解码
			} else if (msgstr.startsWith("{")) {
                Map<Object, Object> temps = JsonUtils.jsonToMap(msgstr);
				params.putAll(temps);
				logger.debug("paramap: \n" + params.toString());
			} else {
				if(SecurityUtils.canCodec()) {
				msgstr = SecurityUtils.decode(msgstr);
				} else {
					msgstr = SecurityUtil.deEncrypt(msgstr);
				}
				Map<Object, Object> temps = JsonUtils.jsonToMap(msgstr);
                params.putAll(temps);
                logger.debug("paramap: \n" + params.toString());

//                for (String s : RSA_KEY_LIST) {
//                    Object keyObj = null;
//                    if(params.containsKey(s) && null != (keyObj = params.get(s)) && keyObj instanceof String) {
//                        params.put(s, RSACerPlus.getInstance().doDecrypt(keyObj.toString()));
//                    }
//                }
            }
		} catch (Exception e) {
			logger.error("解析请求报文异常", e);
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "解析请求报文异常！异常原因为：" + e.getMessage(), transCode);
			return false;
		}
		return true;
	}

	/**
	 * 解析URL中的参数
	 * 
	 * @param request
	 * @param ctxParams
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean parserUrlParams(HttpServletRequest request, Map ctxParams) {
		Enumeration names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			String value = request.getParameter(name);
			if (StringUtil.isNotEmpty(value)) {
				ctxParams.put(name, value);
			}
		}
		return true;
	}
}
