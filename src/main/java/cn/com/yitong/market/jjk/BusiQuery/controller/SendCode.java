package cn.com.yitong.market.jjk.BusiQuery.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.market.jjk.service.CustomFileMngService;
import cn.com.yitong.market.jjk.service.MggImageAttaService;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.SmsUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Controller
public class SendCode extends BaseControl {
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
	@Resource
	private MggImageAttaService mggImageAttaService;
	@Resource
	private CustomFileMngService customFileMngService;

	final String BASE_PATH = "market/jjk/";

	@RequestMapping("jjk/busiQuery/sendCode.do")
	@ResponseBody
	public Map sendCode(HttpServletRequest request,
			HttpServletResponse response){
		String transCode = "busiQuery/sendCode";
		Map rst = new HashMap();
		// 初始化数据总线
				IBusinessContext ctx = CtxUtil.createMapContext(request);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		String idNBR = ctx.getParam("idNBR");
		/**
		 * 获得手机号的接口
		 * 从信审接口中获得手机号码
		 */
		String mobile_no = "";
		//文档开始
		// 服务端生成或读取配置文件 
		Date date = new Date();
		int seq = Integer.valueOf("14");
		int iSeq = seq % 100000;
		String no = DateUtil.todayStr("yyyyMMddHHmmss")
				+ StringUtil.lpadString("" + iSeq, 8, "0");
		String senderSN = no;
		String senderDate = DateUtil.todayStr("yyyy-MM-dd");
		String senderTime = DateUtil.curTimeStr().replaceAll(":", "");

		// 客户端传递的参数 
		//String idNBR = ctx.getParam("idNBR","420682198001130539");	
/*
		Map model = new HashMap();
		model.put("senderDate", senderDate);
		model.put("senderTime", senderTime);
		model.put("senderSN", senderSN);
		model.put("idNBR",idNBR );

		Document resultXml=null;
		Configuration cfg = null;
		try {
			cfg = InterfaceUtil.buildConfiguration("classpath:ftl/");
		} catch (IOException e) {
			logger.error("创建Freemaker Configuration对象时抛出IOException，异常描述信息如下："
					+ e.fillInStackTrace());
		}
		
		if (cfg != null) {
			try {
				Template template = cfg.getTemplate("busiQuery.ftl");
				String xmlString = InterfaceUtil
						.renderTemplate(template, model);
				logger.info("请求网关时的请求报文："+xmlString);
				resultXml = InterfaceUtil.CallInterface(xmlString);
				
				Element ele = resultXml.getRootElement();
				//有效数据区
				Element element = ele.element("Body").element("NoAS400");
				List<Element> elems = element.elements();
				List li = new ArrayList();	
			for(int m = 0; m<elems.size(); m++){
			//	for (Element elem : elems) {
				Element	elem = elems.get(m);
					String name = elem.getName();
					List<Element> sonElems = elem.elements();
					if (null != sonElems && !sonElems.isEmpty()) {
						// 包含子节点
						List array = new ArrayList();
						List<Element> childElems = elem.elements("Map");
						for (Element childMap : childElems) {
							Map child = new HashMap();
							List<Element> subElems = childMap.elements();
							array.add(child);
						}
					} else {
						List attrList = elem.attributes();
							Attribute  item  = (Attribute) attrList.get(0);
						
						logger.info("name:---" + name + "\t text:---"+ elem.getText()+"\t attr:-----"+item.getValue());

						if("appCount".equals(item.getValue())){
							int size = Integer.valueOf(elem.getText());
							for(int i=0;i<size;i++){
								Map map = new HashMap();
								map.put("item", i);
								li.add(map);
							}
						}
						if(m<3){
							if("errorCode".equals(item.getValue())){
								if("0000".equals(elem.getText())){
									rst.put("STATUS", "1");
								}else{
									rst.put("STATUS", "0");
								}
							}else{
								rst.put(item.getValue(), elem.getText());
							}

						}else{
							int scope = (elems.size()-m-1)/11;
							if(scope==0){
								//脱敏
								String field = item.getValue();
								String value = elem.getText();
								
								String fieldValue = null;
								if("custName".equals(field)){
									 fieldValue ="*"+ value.substring(1);
									((Map) li.get(scope)).put(item.getValue(), fieldValue);	
								}else if("idNBR".equals(field)){

									if(value.length()==15){
										 fieldValue = value.substring(0, 7)+"****"+value.substring(13,14);
										 ((Map) li.get(scope)).put(item.getValue(), fieldValue);	
									}else if(value.length()==19){
										 fieldValue = value.substring(0, 10)+"****"+value.substring(15, 19);
										 ((Map) li.get(scope)).put(item.getValue(), fieldValue);	
									}else{
										fieldValue = value.substring(0,14)+"****";
										((Map) li.get(scope)).put(item.getValue(), fieldValue);	
									}

									
								}else{
									((Map) li.get(scope)).put(item.getValue(), elem.getText());	
								}
								
							}
							
						}

					}
					
				}
			//rst.put("LIST", li);
			Map temp = new HashMap();
			temp = (Map) li.get(0);
			mobile_no = (String) temp.get("mobilePhone");
			} catch (Exception e) {
				logger.error("公用类InterfaceUtil抛出异常，异常详细描述信息如下："
						+ e.fillInStackTrace());
			}
		}*/
/*		if("".equals(mobile_no)||mobile_no==null){
			rst.put("STATUS", "0");
			rst.put("MSG", "未获得手机号");
			return rst;
		}else{*/

		String randomCode = SmsUtil.productCode();

		//String STATUS = SendPhoneSms.sendcheckcode(mobile_no,"randomCode");
		//logger.info(STATUS+" "+randomCode+DateUtil.curTimeStr());

		Map map = new HashMap();
		String  time = String.valueOf(date.getTime());
		ctx.saveSessionText("randomCode", randomCode);
		ctx.saveSessionText("time", time);
		ctx.saveSessionText("idNBR", idNBR);
		rst.put("STATUS", "1");
		//rst.put("MSG", STATUS);
		rst.put("MSG", "交易成功！");
		rst.put("randomCode", randomCode);
		rst.put("MOBILE_NO", mobile_no);
		return rst;
		//}

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
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover,requestBuilder, confParser, rst);
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
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
	}

}
