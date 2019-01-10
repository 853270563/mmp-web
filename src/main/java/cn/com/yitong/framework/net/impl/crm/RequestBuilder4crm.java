package cn.com.yitong.framework.net.impl.crm;

import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.consts.AresR;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.service.impl.CommonService;
import cn.com.yitong.framework.util.JSONHelper;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import net.sf.json.JSONObject;

@Component
public class RequestBuilder4crm implements IRequstBuilder {

	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	private CommonService service;
	@Override
	public boolean buildSendMessage(IBusinessContext busiContext, IEBankConfParser confParser, String transCode) {
		// 动态报文体
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (conf != null) {
			logger.info("loaded transconf successfully!" + transCode);
		} else {
			logger.error("loaded transconf failed!" + transCode);
			throw new AresRuntimeException(AppConstants.STATUS_FAIL, "loaded transconf failed!");
		}
		

		return buildDocument(busiContext, conf, transCode);
	}

	/**
	 * 创建请求消息体
	 * 
	 * @param dataMap
	 * @param rst
	 * @return
	 */
	private boolean buildDocument(IBusinessContext ctx, MBTransConfBean conf, String transCode) {
		try {
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("UTF-8");
			Element root = document.addElement(CrmConst.ELEMENT_ROOT);
			List<MBTransItem> sed = conf.getSed();

			// 1.构建报文头
			Element serviceHeader = root.addElement(CrmConst.ELEMENT_HEAD);
			// (1)构建固定报文头
			this.buildStaticHeader(serviceHeader, ctx, transCode, conf);
			// 2.构建报文体
			Element serviceBody = root.addElement(CrmConst.ELEMENT_BODY);
			// 构建动态报文体
			this.buildBody(serviceBody, sed, ctx, conf, transCode);
			//分页
			buildPage(serviceBody, sed, ctx, conf, transCode);
			// 3.格式化XML
			Writer writer = new StringWriter();
			StringUtil.formateXMLStr(writer, document);
			// 4.存储到数据总线
			ctx.setRequestEntry(document.asXML());
			logger.info("格式化显示请求报文:\n" + writer.toString());
			return true;
		} catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error("构建请求消息体异常[交易代码:" + transCode + "]：", e);
		}
		return false;
	}

	private void buildPage(Element serviceBody, List<MBTransItem> sed, IBusinessContext ctx, MBTransConfBean conf, String transCode) {
		// TODO Auto-generated method stub
		Element request = serviceBody.element("request");
		String PAGE_SIZE = ctx.getParam(AresR.PAGE_SIZE);
		String PAGE_NO = "1";
		PAGE_NO = ctx.getParam("PAGE_NO");
		Element page_control = request.addElement("page_control");
		if (!StringUtil.isEmpty(PAGE_SIZE)) {
			page_control.addElement("control_flag").setText("1");
			page_control.addElement("pageindex").setText(PAGE_NO);
			page_control.addElement("pagesize").setText(PAGE_SIZE);
		} else {
			page_control.addElement("control_flag").setText("0");

		}

	}

	/**
	 * @Title: buildHeader
	 * @Description: 构建报文头
	 * @param parent
	 * @param ctx
	 * @param transCode
	 * @return
	 */
	private boolean buildStaticHeader(Element serviceHeader, IBusinessContext ctx, String transCode, MBTransConfBean conf) {
		try {
			String service_sn = service.generyYtServiceSn();
			serviceHeader.addElement("service_sn").setText(service_sn);
			SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyyMMddHHmmss");
			String currentTime = dateformat1.format(new Date());
			serviceHeader.addElement("service_time")
					.setText(currentTime);
			Session session = SecurityUtils.getSession();
			if(session ==null ){
				logger.error("session can not be empty!" + transCode);
				throw new AresRuntimeException(AppConstants.STATUS_FAIL, "session can not be empty!");
			}
			serviceHeader.addElement("version_id").setText("01");//版本号
			serviceHeader.addElement("branch_id").setText((String) session.getAttribute(SessConsts.ORGAN_ID));//原始请求方机构代号
			serviceHeader.addElement("channel_id").setText("39");//服务渠道代号
			serviceHeader.addElement("requester_id").setText("8160097");//请求方系统代码
			serviceHeader.addElement("service_id").setText(conf.getProperty(CrmConst.AT_SERVICE_ID));//ESB服务码
		} catch (Exception e) {
			logger.error("构建固定报文头异常[交易代码:" + transCode + "]：", e);
			return false;
		}
		return true;
	}

	/**
	 * @Title: buildBody
	 * @Description: 构建报文体
	 * @param parent
	 * @param sed
	 * @param ctx
	 * @param conf
	 * @param transCode
	 * @return
	 */
	private boolean buildDynamicHeader(Element serviceHeader, List<MBTransItem> sed, IBusinessContext ctx,
			MBTransConfBean conf, String transCode) {
		try {
			for (MBTransItem item : sed) {
				String name = item.getName();
				String clientName = item.getClient();
				String xpath = item.getXmlPath();
				if (xpath.indexOf(CrmConst.ELEMENT_HEAD) > -1) {
					String text = StringUtil.getString(ctx.getParamMap(), clientName, "");
					Element childElement = serviceHeader.addElement(name);
					if (StringUtil.isNotEmpty(text)) {
						childElement.setText(text);
					} else if (null != item.getDefaultValue()) {
						childElement.setText(item.getDefaultValue());
					}
				}
			}
		} catch (Exception e) {
			logger.error("构建动态报文头异常[交易代码:" + transCode + "]：", e);
			return false;
		}
		return true;
	}

	/**
	 * @Title: buildBody
	 * @Description: 构建报文体
	 * @param parent
	 * @param sed
	 * @param ctx
	 * @param conf
	 * @param transCode
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private boolean buildBody(Element serviceBody, List<MBTransItem> sed, IBusinessContext ctx, MBTransConfBean conf,
			String transCode) {
		Element request = serviceBody.addElement("request");
			for (MBTransItem item : sed) {
				String name = item.getName();
				String clientName = item.getClient();
				String extText = StringUtil.getString(ctx.getParamMap(), clientName, item.getDefaultValue());
				if (StringUtil.isNotEmpty(extText)) {
					request.addElement(name).setText(extText);
				} else if (item.isRequred()) {
					throw new AresRuntimeException(AppConstants.STATUS_FAIL, clientName + ":" + item.getDesc() + "不能为空!");
				}
			}
		return true;
	}

	/**
	 * @Title: buildContent
	 * @Description: 深度构建列表
	 * @param childElement
	 * @param item
	 * @param ctx
	 * @throws Exception 
	 */
	private boolean buildStaticCirculationContent(Element element, MBTransItem item, IBusinessContext ctx,String transCode) throws Exception {
		List<MBTransItem> childItems = item.getChildren();
		
		for (MBTransItem childItem : childItems) {
			int length = childItem.getLength();
			String name = childItem.getName();
			/*	String num = childItem.getNum();
				String loopNum = childItem.getLoop_num();
				String pType = childItem.getP_type();*/
			String clientName = childItem.getClient();
			String type = childItem.getType();
			Element grandsonElement=null;
			if(StringUtil.isNotEmpty(name)){
				grandsonElement = element.addElement(name);
			}
		
			if (CrmConst.FILED_TYPE_S.equals(type)) {
				/*if (StringUtil.isNotEmpty(num)) {
					grandsonElement.addAttribute("id", num);
				}
				if (StringUtil.isNotEmpty(loopNum)) {
					grandsonElement.addAttribute("loop_num", loopNum);
				}
				if (StringUtil.isNotEmpty(pType)) {
					grandsonElement.addAttribute("p_type", pType);
				}*/
				if(!this.buildStaticCirculationContent(grandsonElement, childItem, ctx,transCode)){
					return false;
				}
			}else if (CrmConst.FILED_TYPE_L.equals(type)) {
				String text = StringUtil.getString(ctx.getParamMap(), clientName, "");
				// 右对齐字符串
				text = StringUtil.rpadString(text, length);
				if (StringUtil.isNotEmpty(text)) {
					grandsonElement.setText(text);
				} else if (childItem.isRequred() && StringUtil.isNotEmpty(childItem.getDefaultValue())) {// 此处添加了默认值为空的判断
					grandsonElement.setText(childItem.getDefaultValue());
				} else if(!childItem.isRequred()){// 若默认值为空，且所传参数值为空，则不上传该字段
					element.remove(grandsonElement);
				}
			} else if (CrmConst.FILED_TYPE_B.equals(type)) {
				String text = StringUtil.getString(ctx.getParamMap(), clientName, "");
				if (StringUtil.isNotEmpty(text)) {
					//String encrypt = childItem.getEncryption();
					//	String encryptVal = StringUtil.getString(ctx.getParamMap(), encrypt, "");
					/*if (StringUtil.isNotEmpty(encryptVal)) {
						//	text = thirdService.hsmTranslatePin(text, encryptVal);
						if(StringUtil.isEmpty(text)){
							ctx.setErrorInfo(AppConstants.STATUS_FAIL, "调用加密机转加密失败！", transCode);
							return false;
						}
					}*/
					grandsonElement.setText(text);
				} else if (childItem.isRequred() && StringUtil.isNotEmpty(childItem.getDefaultValue())) {// 此处添加了默认值为空的判断
					grandsonElement.setText(childItem.getDefaultValue());
				} else if(!childItem.isRequred()){// 若默认值为空，且所传参数值为空，则不上传该字段
					element.remove(grandsonElement);
				}
			} else if (CrmConst.FILED_TYPE_P.equals(type)) {
				String text = StringUtil.getString(ctx.getParamMap(), clientName, "");
				if (StringUtil.isNotEmpty(text)) {
					//String encrypt = childItem.getEncryption();
					//	String encryptVal = StringUtil.getString(ctx.getParamMap(), encrypt, "");
					/*if (StringUtil.isNotEmpty(encryptVal)) {
						//	text = thirdService.hsmEncryptPin(text, encryptVal);
						if(StringUtil.isEmpty(text)){
							ctx.setErrorInfo(AppConstants.STATUS_FAIL, "调用加密机加密失败！", transCode);
							return false;
						}
					}*/
					grandsonElement.setText(text);
				} else if (childItem.isRequred() && StringUtil.isNotEmpty(childItem.getDefaultValue())) {// 此处添加了默认值为空的判断
					grandsonElement.setText(childItem.getDefaultValue());
				} else if(!childItem.isRequred()){// 若默认值为空，且所传参数值为空，则不上传该字段
					element.remove(grandsonElement);
				}
			}else if (CrmConst.FILED_TYPE_C.equals(type)) {
				String extText = StringUtil.getString(ctx.getParamMap(), clientName, "");
				if (StringUtil.isNotEmpty(extText)) {
					grandsonElement.setText(extText);
				} else if (childItem.isRequred() && StringUtil.isNotEmpty(childItem.getDefaultValue())) {// 此处添加了默认值为空的判断
					grandsonElement.setText(childItem.getDefaultValue());
				} else if(!childItem.isRequred()){// 若默认值为空，且所传参数值为空，则不上传该字段
					element.remove(grandsonElement);
				} else {
					ctx.setErrorInfo(AppConstants.STATUS_FAIL, clientName + ":" + item.getDesc() + "不能为空!", transCode);
					return false;
				}
			}
		}
		List eles = element.elements();
		if(eles==null || eles.size()<=0){
			element.getParent().remove(element);
		}
		return true;
	}
	
	/**
	 * @Title: buildDynamicCirculationContent
	 * @Description: 构建动态循环内容
	 * @param childElement
	 * @param item
	 * @param ctx
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private boolean buildDynamicCirculationContent(Element childElement, MBTransItem item, List<?> lists,IBusinessContext ctx,String transCode){
		List<MBTransItem> childItems = item.getChildren();
		Element accountElement=null;
		for (int i = 0; i < lists.size(); i++) {
			accountElement  = childElement;
			accountElement  = accountElement.addElement(item.getTargetName());
			accountElement.addAttribute("id",String.valueOf(i+1));
			for (MBTransItem childItem : childItems) {
				int length = childItem.getLength();
				String name = childItem.getName();
				String clientName = childItem.getClient();
				String type = childItem.getType();
				Map<String, Object> resultMap = null;
				Element grandsonElement = accountElement.addElement(name);
				if (lists.get(i) != null && lists.get(i) instanceof Map) {
					if (lists.get(i) instanceof JSONObject) {
						resultMap = JSONHelper
								.reflect((JSONObject) lists.get(i));
					}
					if (lists.get(i) instanceof Map) {
						resultMap = (Map<String, Object>)lists.get(i);
					}
					if (CrmConst.FILED_TYPE_L.equals(type)) {
						String extText="";
						if(childItem.isCtx()){
							extText = StringUtil.getString(ctx.getParamMap(), clientName, "");
						}else{
							extText = StringUtil.getString(resultMap, clientName, "");
						}
						// 右对齐字符串
						extText = StringUtil.rpadString(extText, length);
						if (StringUtil.isNotEmpty(extText)) {
							grandsonElement.setText(extText);
						} else if (childItem.isRequred() && StringUtil.isNotEmpty(childItem.getDefaultValue())) {// 此处添加了默认值为空的判断
							grandsonElement.setText(childItem.getDefaultValue());
						} else if(!childItem.isRequred()){// 若默认值为空，且所传参数值为空，则不上传该字段
							accountElement.remove(grandsonElement);
						}
					} else if (CrmConst.FILED_TYPE_B.equals(type)) {
						String text = StringUtil.getString(resultMap, clientName, "");
						if (StringUtil.isNotEmpty(text)) {
							/*String encrypt = childItem.getEncryption();
							String encryptVal = StringUtil.getString(resultMap, encrypt, "");
							if (StringUtil.isNotEmpty(encryptVal)) {
								text = thirdService.hsmTranslatePin(text, encryptVal);
								if(StringUtil.isEmpty(text)){
									ctx.setErrorInfo(AppConstants.STATUS_FAIL, "调用加密机转加密失败！", transCode);
									return false;
								}
							}*/
							grandsonElement.setText(text);
						} else if (childItem.isRequred() && StringUtil.isNotEmpty(childItem.getDefaultValue())) {// 此处添加了默认值为空的判断
							grandsonElement.setText(childItem.getDefaultValue());
						} else if(!childItem.isRequred()){// 若默认值为空，且所传参数值为空，则不上传该字段
							accountElement.remove(grandsonElement);
						}
					} else if (CrmConst.FILED_TYPE_P.equals(type)) {
						
						String text = StringUtil.getString(resultMap, clientName, "");
						if (StringUtil.isNotEmpty(text)) {
							/*String encrypt = childItem.getEncryption();
							String encryptVal = StringUtil.getString(resultMap, encrypt, "");
							if (StringUtil.isNotEmpty(encryptVal)) {
								text = thirdService.hsmEncryptPin(text, encryptVal);
								if(StringUtil.isEmpty(text)){
									ctx.setErrorInfo(AppConstants.STATUS_FAIL, "调用加密机加密失败！", transCode);
									return false;
								}
							}*/
							grandsonElement.setText(text);
						} else if (childItem.isRequred() && StringUtil.isNotEmpty(childItem.getDefaultValue())) {// 此处添加了默认值为空的判断
							grandsonElement.setText(childItem.getDefaultValue());
						} else if(!childItem.isRequred()){// 若默认值为空，且所传参数值为空，则不上传该字段
							accountElement.remove(grandsonElement);
						}
					}else if (CrmConst.FILED_TYPE_C.equals(type)) {
						String extText="";
						if(childItem.isCtx()){
							extText = StringUtil.getString(ctx.getParamMap(), clientName, "");
						}else{
							extText = StringUtil.getString(resultMap, clientName, "");
						}
						if (StringUtil.isNotEmpty(extText)) {
							grandsonElement.setText(extText);
						} else if (childItem.isRequred() && StringUtil.isNotEmpty(childItem.getDefaultValue())) {// 此处添加了默认值为空的判断
							grandsonElement.setText(childItem.getDefaultValue());
						} else if(!childItem.isRequred()){// 若默认值为空，且所传参数值为空，则不上传该字段
							accountElement.remove(grandsonElement);
						}
					}
				}
			}
		}
		return true;
	}
}
