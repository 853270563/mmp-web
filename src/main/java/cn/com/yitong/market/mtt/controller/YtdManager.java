package cn.com.yitong.market.mtt.controller;

import java.sql.Clob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.ares.login.BizLogger;
import cn.com.yitong.util.ClobTransfer;
import cn.com.yitong.util.YTLog;
@Controller
public class YtdManager extends BaseControl {
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

	final String BASE_PATH = "market/mtt/preForm/";

    final BizLogger bizLogger = BizLogger.getLogger(this.getClass());

	@RequestMapping("mtt/preForm/{trans_code}.do")	
	@ResponseBody
	public Map<String,Object> ytdManager(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			params.put("FORM_STATUS", "0");
			Map<String,Object> data = service.load(statementName, params);
			if(data.isEmpty() || null==data.get("FORM_CONTENT")){
				ok=true;
				return rst;
			}
			List<Map<String,Object>> formData = new ArrayList<Map<String,Object>>();
			String formContentStr = ClobTransfer.clobToString((Clob)data.get("FORM_CONTENT"));
			String[] formContent = formContentStr.toString().split("\n");
			for(String fc : formContent){
				if (fc.isEmpty())
					continue;
				String datas[] = fc.split(",");
				String optionData[] = datas[4].replace(" ", "").split("\\|");
                StringBuffer optionDataSb =new StringBuffer();
                for (int i =0;i<optionData.length;i++){
                    optionDataSb.append(optionData[i]);
                }
				Map<String,Object> formInfo = new HashMap<String,Object>();
				formInfo.put("NAME", datas[1]);
				formInfo.put("FORM_NAME", datas[2]);
				// 字段类型
				if (datas[3].equals("1")||datas[3].equals("6")) {
					formInfo.put("FORM_TYPE", "text");
				}else if (datas[3].equals("2")) {
					formInfo.put("FORM_TYPE", "select");
					// 字段内容
					formInfo.put("FORM_LIST", optionDataSb.length()>0?optionDataSb:" ");
				}else if (datas[3].equals("3")) {
					formInfo.put("FORM_TYPE", "checkbox");
					// 字段内容
				}else if (datas[3].equals("4")) {
					formInfo.put("FORM_TYPE", "date");
				}else if (datas[3].equals("5")) {
					formInfo.put("FORM_TYPE", "number");
				}
				// 字段长度
				formInfo.put("FIELDSIZE", datas[5]);
				// 是否需要验证预填单
				if (datas[6].equals("1")) {
					formInfo.put("PRETREATMENT", "true");
//                    formInfo.put("PRETREATMENT_VALUE",datas[8]);
				}else if (datas[6].equals("2")) {
					formInfo.put("PRETREATMENT", "false");
				}

				// 是否必输项
				if (datas[7].equals("1")) {
					formInfo.put("FORM_VALIDATE", "true");
				}else if (datas[7].equals("2")) {
					formInfo.put("FORM_VALIDATE", "false");
				}
				formData.add(formInfo);
			}
			ok=true;
            rst.put("SUBMIT_URL", "http://yitong.com.cn");
			rst.put("FORMDATA", formData);
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	
	@RequestMapping("mtt/preFormType/{trans_code}.do")	
	@ResponseBody
	public Map<String,Object> formTypeList(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			params.put("TYPE", "formType");
			List<Map<String,Object>> data = service.findList(statementName, params);
			rst.put("FORM_LIST", data);
			ok=true;
			bizLogger.info("交易成功！", "100402");
		} catch (Exception e) {
			// 输出错误的关键信息
			transAfter(ctx, transCode, rst, false);
			rst.put("MSG", "交易失败!");
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	
	/**
	 * 事务前置处理
	 * 
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @return
	 */
	private boolean transPrev(IBusinessContext ctx, String transCode, Map rst) {
		// 交易开始，设置交易流水
		commonService.generyTransLogSeq(ctx, transCode);
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
	private void transAfter(IBusinessContext ctx, String transCode, Map rst,
			boolean ok) {
		// 生成交易结果
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		// 保存交易日志
		commonService.saveJsonTransLog(ctx, transCode, rst);
	}
}
