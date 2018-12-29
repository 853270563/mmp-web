package cn.com.yitong.modules.ares.login.controller;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.modules.ares.login.BizLogger;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ChangePwdController extends BaseControl {

	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	ICrudService service;
	@Autowired
	@Qualifier("requestBuilder4db")
	IRequstBuilder requestBuilder;// 请求报文生成器
	@Autowired
	@Qualifier("responseParser4db")
	IResponseParser responseParser;// 响应报文解析器
	@Autowired
	@Qualifier("urlClient4db")
	IClientFactory client;// 响应报文解析器
	@Autowired
	@Qualifier("EBankConfParser4db")
	IEBankConfParser confParser;// 报文装载器

	final String BASE_PATH = "ares/login/";

    final BizLogger bizLogger = BizLogger.getLogger(this.getClass());

	@RequestMapping("ares/login/ChangePwd.do")
	@ResponseBody
	public Map modi(HttpServletRequest request) {
		String trans_code = "ChangePwd";
		bizLogger.info("用户密码变更申请", "100401");
		String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			bizLogger.warn("用户密码变更失败,失败原因:"+(String)rst.get("MSG"), "100403");
			return rst;
		}
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			bizLogger.warn("用户密码变更失败,失败原因:"+(String)rst.get("MSG"), "100403");
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		Map param = new HashMap();

		String USER_ID = params.get("USER_ID").toString();
		param.put("USER_ID", USER_ID);
		Map datas = service.load("SYS_USER.CheckPwd", param);
		String pwd = datas.get("USER_PSW").toString();
		String USER_NEWPSW = params.get("USER_NEWPSW").toString();
		String USER_PSW = params.get("USER_PSW").toString();
		if (pwd.equalsIgnoreCase(USER_PSW)) {
			params.put("USER_PSW", USER_NEWPSW.toUpperCase());
			params.put("USER_ID", USER_ID);
			boolean ok = false;
			try {
				// 数据库操作
				String statementName = CtxUtil.getStatement(confParser, transCode);
				ok = service.update(statementName, params);
				bizLogger.info("用户密码变更成功", "100402");
			} catch (Exception e) {
				// 输出错误的关键信息
				transAfter(ctx, transCode, rst, false);
				rst.put("MSG", "修改失败!");
				logger.error(ctx.getTransLogBean(transCode), e);
				bizLogger.warn("用户密码变更失败" , "100403");
			}
			transAfter(ctx, transCode, rst, ok);
		}else{
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "原密码错误, 修改失败!");
			bizLogger.warn("用户密码变更失败, 失败原因:" +  "原密码错误", "100403");
		}
		return rst;
	}
	
	/**
	 * 事务前置处理
	 */
	private boolean transPrev(IBusinessContext ctx, String transCode, Map rst) {
		// 交易开始，设置交易流水
		commonService.generyTransLogSeq(ctx, transCode);
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst);
	}

	/**
	 * 事务之后处理
	 */
	private void transAfter(IBusinessContext ctx, String transCode, Map rst, boolean ok) {
		// 生成交易结果
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		// 保存交易日志
		commonService.saveJsonTransLog(ctx, transCode, rst);
	}
}
