package cn.com.yitong.modules;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.ares.core.AresApp;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.flow.IFlowTool;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.MessageTools;

/**
 * 请求处理-交易路由控制器<br>
 * 1、处理器交易模型，按执行方式，分为两种：<br>
 * 1) IEasyController ：execute(ctx)，适用于特定用途的交易模型<br>
 * 2) IRuleController ：execute(ctx,transCode) ，适用于多个交易码共用一个交易模型<br>
 * <br>
 * 2、处理类适配方式，根据交易路径URL目录层级分别为： <br>
 * 1) 一层路径：beanId ==>IEasyController<br>
 * 2) 二层路径：dir/beanId ==>IEasyController<br>
 * 3) 三层路径：dir/beanId/code ==>IRuleController<br>
 * 4) 更多层路径为非法请求路径；<br>
 * 
 * @author yaoym
 * 
 */
//@Controller
public class FlowController extends BaseControl {
	private Logger logger = LoggerFactory.getLogger(getClass());


	@Autowired
	IFlowTool flowTool;

	@SuppressWarnings("unchecked")
    @ResponseBody
	@RequestMapping("**")
	public Map<?, ?> execute(HttpServletRequest request) {
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		//获取请求url
		String url = request.getServletPath();
		//分割请求Url
		String fullTransCode = url.substring(1).split("\\.")[0];
		if (!ctx.initParamCover(json2MapParamCover, fullTransCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(fullTransCode), ctx.getParamMap());
			return ctx.getParamMap();
		}
		try {
			// TODO:交易前处理 交易检查
			String[] paths = fullTransCode.split("/");
			int size = paths.length;
			if (size < 2) {
				return errorService();
			}else if(size == 2){
				ctx.setParam("transCode", paths[0]+"/"+paths[1]);
			}else if(size > 2){
				ctx.setParam("transCode", paths[0]+"/"+paths[2]);
			}
			String flowPath = String.format("%s/%s", paths[0], paths[1]);
			flowTool.execute(ctx, flowPath);
			return CtxUtil.showSuccessResult(ctx.getParamMap());
		} catch (BeansException e) {
			logger.warn("service not found: {},error:{}", url, e.getMessage());
			return errorService();
		} catch (AresRuntimeException e) {
			logger.error(e.getMessage(), e);
			return CtxUtil.showErrorResult(e, ctx);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return CtxUtil.showErrorResult(e, ctx);
		}
	}


	private final String SERCICE_ERROR = "inte.service_not_found_or_error";
	Map<String, Object> errorService() {
		Map<String, Object> out = new HashMap<String, Object>();
		out.put(AppConstants.STATUS, SERCICE_ERROR);
		out.put(AppConstants.MSG, AresApp.getInstance().getMessage(SERCICE_ERROR));
		return out;
	}
}
