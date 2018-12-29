package cn.com.yitong.framework.core.bean;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.core.base.WebUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IParamCover;
import cn.com.yitong.framework.service.ICommonService;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 基础控制类
 * 定义交易共享的方法放置基中
 * @author yaoym
 */
public abstract class BaseControl {

	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	protected BusinessContextFactory busiCtxFactory;
	@Autowired
	@Qualifier("jsonParamCover")
	protected IParamCover jsonParamCover;
	@Autowired
	@Qualifier("json2MapParamCover")
	protected IParamCover json2MapParamCover;
	@Autowired
	protected ICommonService commonService;

	/**
	 * 设置交易失败信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void responseFailure(Map rst, String status, String msg) {
		rst.put(AppConstants.STATUS, status);
		rst.put(AppConstants.MSG, msg);
	}

	/**
	 * 设置交易成功信息
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void responseSuccess(Map rst, String msg) {
		rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
		rst.put(AppConstants.MSG, msg);
	}

	/**
	 * 获取多语言路径前缀
	 */
	protected String getPrexPath(IBusinessContext busiCtx) {
		String prex = "";
		String language = busiCtx.getSessionText(NS.LANGUAGE);
		if (AppConstants.ZH_CN.equals(language)) {
			prex = "auto_zh/";
		} else if (AppConstants.EN.equals(language)) {
			prex = "auto_en/";
		}
		return prex;
	}

    @ExceptionHandler({Exception.class})
    public void exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        WebUtils.jsonExceptionHandler(response, e);
    }
}
