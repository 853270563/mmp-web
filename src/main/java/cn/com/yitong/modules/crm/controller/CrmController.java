package cn.com.yitong.modules.crm.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.INetTools;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.MessageTools;

/**
 * @author luanyu
 * @date   2018年6月12日
 */
@Controller
public class CrmController extends BaseControl {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static SqlSession sqlSession = SpringContextUtils.getBean(SqlSession.class);
	@Autowired
	@Qualifier("netTools4crm")
	INetTools netTool;
	/**
	 * 公用交易
	 * @param request
	 * @return
	 */
	@RequestMapping("**")
	@ResponseBody
	public Map<String, Object> getMeau(HttpServletRequest request) {
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);

		String url = request.getServletPath();
		String fullTransCode = url.substring(1).split("\\.")[0];
		String[] paths = fullTransCode.split("/");
		int size = paths.length;
		if (size < 2) {
			return errorService();
		}
		// 交易处理
		String transCode = String.format("%s/%s", paths[0], paths[1]);
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), ctx.getParamMap());
			return ctx.getParamMap();
		}
		try {

			netTool.execute(ctx, transCode);
			return CtxUtil.showSuccessResult(ctx.getParamMap());
		} catch (AresRuntimeException e) {
			logger.error(e.getMessage(), e);
			return CtxUtil.showErrorResult(e, ctx);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ctx.setParam(AppConstants.STATUS, AppConstants.STATUS_FAIL);
			ctx.setParam(AppConstants.MSG, e.getMessage());
			return ctx.getParamMap();
		}
	}

	Map<String, Object> errorService() {
		Map<String, Object> out = new HashMap<String, Object>();
		out.put(AppConstants.STATUS, AppConstants.ERR_CODE);
		out.put(AppConstants.MSG, "inte.service_not_found_or_error");
		return out;
	}
}
