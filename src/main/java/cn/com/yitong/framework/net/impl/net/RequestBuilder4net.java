package cn.com.yitong.framework.net.impl.net;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.core.vo.MBTransItem;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Component
public class RequestBuilder4net implements IRequstBuilder {

	private Logger logger = YTLog.getLogger(this.getClass());
	
	@Override
	public boolean buildSendMessage(IBusinessContext busiContext,
			IEBankConfParser confParser, String transCode) {
		logger.info("RequestBuilder4cupd.buildSendMessage33 ......................"
				+ transCode);
		// 动态报文体
		MBTransConfBean conf = confParser.findTransConfById(transCode);
		if (conf != null) {
			logger.info("loaded transconf successfully!" + transCode);
		} else {
			logger.error("loaded transconf failed!" + transCode);
			busiContext.setErrorInfo(AppConstants.STATUS_FAIL, "交易定义加载失败!",
					transCode);
			return false;
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
	private boolean buildDocument(IBusinessContext ctx, MBTransConfBean conf,
			String transCode) {
		logger.info("RequestBuilder4cupd.buildDocument ......................");
		try {
			String requestStr = (String) ctx.getRequestEntry();
			logger.info("response message is:\n" + requestStr);
			Document doc = null;
			try {
				doc = DocumentHelper.parseText(requestStr);
			} catch (DocumentException e) {
				logger.error(transCode + " receive data can't covert to xml", e);
				ctx.setErrorInfo(AppConstants.STATUS_FAIL, "交易响应解析失败",transCode);
				return false;
			}
			
			MessageTools.elementToMap(ctx.getResponseContext(transCode),ctx.getParamMap());
			//判断成功失败
			String status =ctx.getParam(AppConstants.STATUS)==null?"0":ctx.getParam(AppConstants.STATUS);
			String msg =ctx.getParam(AppConstants.MSG)==null?"交易失败":ctx.getParam(AppConstants.MSG);
			if("1".equals(status)){
				ctx.setParam("status", "COMPLETE");
				ctx.setParam("RetCode", "0");
				ctx.setParam("ErrMsg", "交易成功");
			}else{
				ctx.setParam("status", "FAIL");
				ctx.setParam("RetCode", "1");
				ctx.setParam("ErrMsg", msg);
			}
			//构建动态报文体
			this.buildBody(doc, ctx, conf, transCode);
			
			//3.格式化XML
			Writer writer = new StringWriter();
			StringUtil.formateXMLStr(writer,doc);
			String result = writer.toString();
			
			//4.存储到数据总线
			ctx.setResponseEntry(doc.asXML());
			
			logger.info("格式化显示响应报文:\n" + result);
			return true;
		} catch (Exception e) {
			logger.error("构建响应报文体异常[交易代码:"+transCode+"]：", e);
		}
		ctx.setErrorInfo(AppConstants.STATUS_FAIL, "响应报文体异常!", transCode);
		return false;
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
	private boolean buildBody(Document doc, IBusinessContext ctx, MBTransConfBean conf, String transCode) {
		try {
			List<MBTransItem> rvc = conf.getRcv();
			for (MBTransItem item : rvc) {
				String type = item.getType();
				String name = item.getName();
				String clientName = item.getTargetName();
				clientName = StringUtil.isEmpty(clientName) ? name : clientName;
				String xpath = item.getXmlPath();
				if (EBankConstnet.FILED_TYPE_E.equals(type)) {
					Element element = (Element)doc.getRootElement().selectSingleNode(xpath);
					element = element.addElement(name);
					buildContent(element,item,ctx);
				}
			}
		} catch (Exception e) {
			logger.error("构建报文体异常[交易代码:"+transCode+"]：", e);
			return false;
		}
		return true;
	}
	
	/**
	 * @Title: buildContent
	 * @Description: 深度构建列表
	 * @param childElement
	 * @param item
	 * @param ctx
	 */
	private void buildContent(Element childElement, MBTransItem item,IBusinessContext ctx){
		List<MBTransItem> childItems = item.getChildren();
		for (MBTransItem childItem : childItems) {
			String name = childItem.getName();
			String clientName = childItem.getTargetName();
			clientName = StringUtil.isEmpty(clientName) ? name : clientName;
			if(EBankConstnet.FILED_TYPE_C.equals(childItem.getType())){
				String extText = StringUtil.getString(ctx.getParamMap(), clientName, "");
				if(StringUtil.isNotEmpty(extText)){
					childElement.addElement(name).setText(extText);
				}else if(childItem.isRequred() && StringUtil.isEmpty(extText)){
					childElement.addElement(name).setText(EBankConstnet.EMPTY);
				}
			}
		}
	}
}
