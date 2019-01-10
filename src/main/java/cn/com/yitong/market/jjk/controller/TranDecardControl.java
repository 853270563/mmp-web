package cn.com.yitong.market.jjk.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.core.base.WebUtils;
import cn.com.yitong.core.base.dao.Criteria;
import cn.com.yitong.core.base.dao.CriteriaExample;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.DBaseControl;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.market.jjk.model.JjkTranCard;
import cn.com.yitong.market.jjk.service.JjkTranCardService;
import cn.com.yitong.util.ConfigEnum;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.StringUtil;

@Controller
@RequestMapping("/jjk/trandecard")
public class TranDecardControl extends DBaseControl {

	private static final String BASE_PATH = "market/jjk/debit/";

	@Resource
	private JjkTranCardService jjkTranCardService;

	@RequestMapping("queryBusiCount")
	@ResponseBody
	public Map<String, Object> queryBusiCount(HttpServletRequest req) {
		String transCode = BASE_PATH + "QueryBusiCount";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(req);
		ctx.initParamCover(json2MapParamCover, transCode, false);
		if (!requestBuilder.buildSendMessage(ctx, confParser, transCode)) {
			// 请求报文检查失败
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		
		String userId = WebUtils.getCurrentUserId(req, ctx);
		if(null == userId) {
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "未获取到用户信息，请登录后再操作", transCode);
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		rst.put("SUCCESS_CHK_COUNT", jjkTranCardService.queryCount(userId, true));
		rst.put("FAIL_CHK_COUNT", jjkTranCardService.queryCount(userId, false));
		
		// 检查响应报文,过滤冗余输出，并可解析字典数据
		ctx.setSuccessInfo(AppConstants.STATUS_OK, "", transCode);
		ctx.setResponseEntry(rst);
		responseParser.parserResponseData(ctx, confParser, transCode);
		MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
		return rst;
	}
	
	@RequestMapping("cardInfoAdd")
	@ResponseBody
	public Map<String, Object> cardInfoAdd(HttpServletRequest req)
			throws SQLException, FileNotFoundException, IOException {
		String transCode = BASE_PATH + "CardInfoAdd";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(req);
		ctx.initParamCover(json2MapParamCover, transCode, false);
		if (!requestBuilder.buildSendMessage(ctx, confParser, transCode)) {
			// 请求报文检查失败
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		JjkTranCard cardInfo = new JjkTranCard();
		try {
			BeanUtils.copyProperties(cardInfo, ctx.getParamMap());
		} catch (Exception e) {
			logger.error("参数转换失败！", e);
			ctx.setErrorInfo(AppConstants.STATUS_FAIL, "参数转换有误，请联系管理员", transCode);
			e.printStackTrace();
		}
		cardInfo.setSignState(ConfigEnum.DICT_TRAN_DECARD_SIGN_STATE_SUBMIT
				.strVal());
		jjkTranCardService.insert(cardInfo);
		rst.put("transId", cardInfo.getTransId());
		
		// 检查响应报文,过滤冗余输出，并可解析字典数据
		ctx.setSuccessInfo(AppConstants.STATUS_OK, "", transCode);
		ctx.setResponseEntry(rst);
		responseParser.parserResponseData(ctx, confParser, transCode);
		MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
		return rst;
	}
	
	@RequestMapping("selfBusiQuery")
	@ResponseBody
	public Map<String, Object> selfBusiQuery(HttpServletRequest request) {
		String transCode = BASE_PATH + "SelfBusiQuery";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		ctx.initParamCover(json2MapParamCover, transCode, false);
		if (!requestBuilder.buildSendMessage(ctx, confParser, transCode)) {
			// 请求报文检查失败
			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			return rst;
		}
		Date startDate = StringUtil.isBlank(ctx.getParam("startDate")) ? new Date() : DateUtil.parseDate(ctx.getParam("startDate"), "yyyy/MM/dd");
		Date endDate = StringUtil.isBlank(ctx.getParam("endDate")) ? new Date() : DateUtil.parseDate(ctx.getParam("endDate"), "yyyy/MM/dd");
		endDate = new Date(endDate.getTime()+24*60*60*1000);
		String loginId = WebUtils.getCurrentUserId(request, ctx);
		CriteriaExample<JjkTranCard> example = new CriteriaExample<JjkTranCard>();
		Criteria crit = example.createCriteria();
		crit.between(JjkTranCard.FL.signTime, startDate, endDate);
		crit.equalTo(JjkTranCard.FL.signUser, loginId) ;
		List<JjkTranCard> list = jjkTranCardService.queryByExampleExt(example);
		
		// 检查响应报文,过滤冗余输出，并可解析字典数据
		ctx.setSuccessInfo(AppConstants.STATUS_OK, "", transCode);
		ctx.setResponseEntry(rst);
		responseParser.parserResponseData(ctx, confParser, transCode);
		MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
		
		rst.put("list", list);
		return rst;
	}

}
