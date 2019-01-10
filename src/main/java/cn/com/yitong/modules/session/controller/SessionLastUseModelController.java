package cn.com.yitong.modules.session.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IParamCover;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;

import cn.com.yitong.core.base.web.BaseController;

/**
 * 提供更新最后一次使用功能的接口
 * @author zhuzengpeng<zzp@yitong.com.cn>
 */
@Controller
@RequestMapping("/session/lastUse")
public class SessionLastUseModelController extends BaseController{

	@Autowired
	@Qualifier("requestBuilder4db")
	private IRequstBuilder requestBuilder;// 请求报文生成器
	
	@Autowired
	@Qualifier("responseParser4db")
	private IResponseParser responseParser;// 响应报文解析器
	
	@Autowired
	@Qualifier("EBankConfParser4db")
	private IEBankConfParser confParser;// 报文装载器

	@Autowired
	@Qualifier("json2MapParamCover")
	private IParamCover json2MapParamCover;
	
	@Autowired
	private ICrudService crudService;
	
	/**
	 * 提交最后一次使用功能并更新至SESSION表中
	 * @param request
	 * @return
	 */
	@RequestMapping("/commitLastUseModel.do")
	@ResponseBody
	public Map<String, Object> commitLastUseModel(HttpServletRequest request) {
		String transCode = "session/commitLastUseModel";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst)) {
			return rst;
		}
		//开始更新SESSION信息表事件ID
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String eventId = request.getParameter("EVENT_ID");
		paramMap.put("eventId", eventId);
		SecurityUtils.getSessionRequired().setEventId(eventId);
		CtxUtil.transAfter(ctx, transCode, rst, true, responseParser, confParser);
		return rst;
	}
}
