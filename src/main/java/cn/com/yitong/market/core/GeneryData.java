package cn.com.yitong.market.core;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

/**
 * 返回数据
 * 
 * @author yaoym
 * 
 */
@Controller
public class GeneryData extends BaseControl {

	private Logger logger = YTLog.getLogger(this.getClass());

	@RequestMapping("common/data/{trans_code}.do")
	@ResponseBody
	public Map execute(@PathVariable String trans_code,
			HttpServletRequest request) {
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext busiCtx = busiCtxFactory.createBusiContext(request);
		// 加载参数
		busiCtx.initParamCover(jsonParamCover, false);
		doTrans(busiCtx, request, trans_code, rst);
		if (logger.isDebugEnabled()) {
			logger.debug(">> base context:\n"
					+ StringUtil.formatXmlStr(busiCtx.getBaseContext().asXML()));
			logger.debug(">> result info:\n" + rst.toString());
		}

		return rst;
	}

	public boolean doTrans(IBusinessContext busiCtx,
			HttpServletRequest request, String transCode, Map rst) {
		busiCtx.setParam("trans_code", transCode);
		this.execute(busiCtx, request, transCode);
		MessageTools.elementToMap(busiCtx.getResponseContext(transCode), rst);
		return true;
	}

}
