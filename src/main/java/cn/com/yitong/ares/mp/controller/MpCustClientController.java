package cn.com.yitong.ares.mp.controller;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MpCustClientController extends BaseControl {

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

	final String BASE_PATH = "ares/mp/";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("ares/mp/mpCustClientSub.do")
	@ResponseBody
	public Map<String, String> mpCustClientSub(HttpServletRequest request) {

		String transCode = BASE_PATH + "mpCustClientSub";
		Map<String, String> resultMap = new HashMap<String, String>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, resultMap)) {
			return resultMap;
		}
		// 数据库操作区
		Map<String, Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			Map appMap = service.load("MP_CUST_CLIENT.checkAppId", params);
			if (null != appMap && params.get("CLIENT_APP_ID").equals(appMap.get("CLIENT_APP_ID"))) {
				// 获取主键
				Map<String, String> keyMap = service.load("MP_CUST_CLIENT.genCustClientId", null);
				params.putAll(keyMap);
				resultMap.putAll(keyMap);
				HttpSession session = request.getSession();
                //客户端消息主题编号
                String clientId=params.get("CUST_ID")+"_"+params.get("DEVICE_ID");
                params.put("CLIENT_ID",clientId);
				params.put("CREATE_TIME", new Date());
				params.put("UPDATE_TIME", new Date());
                params.put("OPER_ID", (String) session.getAttribute("LGN_ID"));
				//params.put("OPER_ID", "admin");
				params.put("DEL_FLAG", "0");
				service.delete("MP_CUST_CLIENT.deleteCust", params);
				ok = service.insert("MP_CUST_CLIENT.addSub", params);
                resultMap.put("CLIENT_ID",clientId);
			} else {
                logger.info(" - - - - - - - - - -客户端应用标识不存在!  " +"编号="+ (String)params.get("CLIENT_APP_ID"));
				ok = false;
			}
		} catch (Exception ex) {
			ok = false;
			logger.error(ctx.getTransLogBean(transCode), ex);
		}
		transAfter(ctx, transCode, resultMap, ok);
		return resultMap;
	}

	@RequestMapping("ares/mp/mpCustClientUpdate.do")
	@ResponseBody
	public Map<String, String> mpCustClientUpdate(HttpServletRequest request) {
		String transCode = BASE_PATH + "mpCustClientUpdate";
		Map<String, String> resultMap = new HashMap<String, String>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, resultMap)) {
			return resultMap;
		}

		// 数据库操作区
		Map<String, Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			HttpSession session = request.getSession();
			params.put("UPDATE_TIME", new Date());
            params.put("OPER_ID", (String) session.getAttribute("LGN_ID"));
			//params.put("OPER_ID", "admin");

			ok = service.update("MP_CUST_CLIENT.modifySub", params);
			resultMap.put("IS_CLOSE", params.get("IS_CLOSE").toString());
		} catch (Exception ex) {
			ok = false;
			logger.error(ctx.getTransLogBean(transCode), ex);
		}
		transAfter(ctx, transCode, resultMap, ok);

		return resultMap;
	}

	/**
	 * 事务前置处理
	 * 
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @return
	 */
	private boolean transPrev(IBusinessContext ctx, String transCode,
			Map<String, String> rst) {
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst);
	}

	/**
	 * 事务之后处理
	 * 
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	private void transAfter(IBusinessContext ctx, String transCode,
			Map<String, String> rst,
			boolean ok) {
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
	}
}
