package cn.com.yitong.framework.core.bean;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IParamCover;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Component
public class JsonParamCover implements IParamCover {
	private Logger logger = YTLog.getLogger(this.getClass());

	public boolean cover(IBusinessContext ctx, HttpServletRequest request,
			boolean signed, String transCode) throws Exception {
		if (null == request || ctx == null)
			return false; 
		Element context = ctx.getParamContext();
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
			return false;
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
			} else if (msgstr.startsWith("{")) {
				JSONObject json = new JSONObject(msgstr);
				// 加载JSON请求
				MessageTools.jsonToElement(context, json);
			}
		} catch (JSONException e) {
			logger.error("request json str buffer is error!", e);
		}
		return true;
	}
}
