package cn.com.yitong.framework.core.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.core.util.ThreadContext;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.base.IMBElement;
import cn.com.yitong.framework.core.vo.TransLogBean;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IParamCover;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.service.ICommonService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.StringUtil;

/**
 * 统一资源上下文 ，业务总线 reqeust|session|application data selectNode
 * 
 * @author yaoym
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class BusinessContext implements IBusinessContext {
	private HttpServletRequest request;
	private HttpSession session;
	// 公共数据
	private Map<String, String> header = new HashMap();
	private Element root;
	// 请求参数区域
	private Element params;
	private Map paraMap;
	// 临时请求内容
	private Object requestEntry;
	// 临时响应内容
	private Object responseEntry;

	private String status;
	// 系统响应码
	private String rspCode;
	// 系统响应消息
	private String rspMsg;

	private TransLogBean transLogBean;

	public BusinessContext() {
		init();
	}

	public BusinessContext(HttpServletRequest request) {
		this.request = request;
		this.session = request.getSession();
		init();
	}
	public BusinessContext(String type) {
		this.paramType = type;
		init();
	}
	public BusinessContext(HttpServletRequest request, String type) {
		this.request = request;
		this.session = request.getSession();
		this.paramType = type;
		init();
	}

	private void init() {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("utf-8");
		root = doc.addElement("root");
		// 请求参数
		if (isMapParamType()) {
			paraMap = new HashMap();
		} else {
			params = root.addElement("params");
		}
	}

	@Override
	public boolean initParamCover(boolean signed) {
		return initParamCover(new JsonParamCover(), signed);
	}

	@Override
	public boolean initParamCover(IParamCover paramCover, boolean signed) {
		return initParamCover(paramCover, null, signed);
	}

	@Override
	public boolean initParamCover(IParamCover paramCover, String transCode, boolean signed) {
		try {
			ThreadContext.put(CtxUtil.TRANS_CODE_KEY, transCode);
			ThreadContext.put(CtxUtil.BUSINESS_CONTEXT_KEY, this);

			IParamCover pcover = paramCover;
			if (pcover == null) {
				pcover = new JsonParamCover();
			}
			if (!pcover.cover(this, request, signed, transCode)) {
				return false;
			}
			String cifNo = this.getSessionText(SessConsts.CIF_NO);
			if (StringUtil.isNotEmpty(cifNo)) {
				setParam(NS.CIF_NO, cifNo);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * 将总线内的数据全部输出成字符串
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getBaseContext().asXML();
	}

	@Override
	public Element getRequestContext(String transCode) {
		Element rctx = (Element) root.selectSingleNode("requestContext[@transCode='" + transCode + "']");
		if (rctx == null) {
			rctx = root.addElement("requestContext");
			rctx.addAttribute("transCode", transCode);
		}
		return rctx;
	}

	@Override
	public Element getResponseContext(String transCode) {
		Element rctx = (Element) root.selectSingleNode("responseContext[@transCode='" + transCode + "']");
		if (rctx == null) {
			rctx = root.addElement("responseContext");
			rctx.addAttribute("transCode", transCode);
		}
		return rctx;
	}

	@Override
	public Element getRequestContext() {
		return root.element("requestContext");
	}

	@Override
	public Element getResponseContext() {
		return root.element("responseContext");
	}

	@Override
	public String getResponseStatus() {
		return status;
	}

	@Override
	public void setErrorInfo(String errCode, String errMsg, String transCode) {
		Element rspCtx = getResponseContext(transCode);
		saveNode(rspCtx, AppConstants.STATUS, errCode);
		saveNode(rspCtx, AppConstants.MSG, errMsg);
		this.status = errCode;
	}

	@Override
	public void setSuccessInfo(String sucCode, String sucMsg, String transCode) {
		Element rspCtx = getResponseContext(transCode);
		saveNode(rspCtx, AppConstants.STATUS, sucCode);
		saveNode(rspCtx, AppConstants.MSG, sucMsg);
		this.status = sucCode;
	}

	@Override
	public Element saveNode(Element parent, String name, String text) {
		Element element = (Element) parent.selectSingleNode(name);
		if (element == null) {
			element = parent.addElement(name);
		}
		element.setText(text);
		return element;
	}

	@Override
	public Element addNode(Element parent, String name, String text) {
		Element element = parent.addElement(name);
		element.setText(text);
		return element;
	}

	@Override
	public String getParam(String xpath) {
		if (isMapParamType()) {
			return StringUtil.getString(paraMap, xpath, "");
		}
		Node node = params.selectSingleNode(xpath);
		if (node != null)
			return node.getText();
		return null;
	}

	@Override
	public List getParamDatas(String xpath) {
		if (isMapParamType()) {
			return (List) paraMap.get(xpath);
		}
		return params.selectNodes(xpath);
	}

	@Override
	public boolean setParam(String xpath, Object text) {
		if (isMapParamType()) {
			paraMap.put(xpath, text);
			return true;
		}
		Node node = params.selectSingleNode(xpath);
		if (node != null) {
			node.setText(StringUtil.isEmpty(text.toString()) ? "" : text.toString());
		} else {
			params.addElement(xpath).setText(StringUtil.isEmpty(text.toString()) ? "" : text.toString());
		}
		return true;
	}

	@Override
	public String getSessionText(String name) {
		return (String) session.getAttribute(name);
	}

	@Override
	public Element getSessionElement(String name) {
		return null;
	}

	@Override
	public void saveSessionText(String name, Object value) {

		session.setAttribute(name, value);
	}

	@Override
	public void saveSessionList(String name, List<Map> value) {

		session.setAttribute(name, value);
	}

	@Override
	public List<Map> getSessionList(String name) {

		return (List<Map>) session.getAttribute(name);
	}

	@Override
	public boolean saveSession(IMBElement elem) {
		session.setAttribute(elem.getName(), elem.generyObject());
		return true;
	}

	@Override
	public Element getBaseContext() {
		return root;
	}

	@Override
	public Element getParamContext() {
		return params;
	}

	@Override
	public HttpSession getHttpSession() {
		return session;
	}

	@Override
	public Object getRequestEntry() {
		return requestEntry;
	}

	@Override
	public void setRequestEntry(Object requestEntry) {
		this.requestEntry = requestEntry;
	}

	@Override
	public Object getResponseEntry() {
		return responseEntry;
	}

	@Override
	public void setResponseEntry(Object responseEntry) {
		this.responseEntry = responseEntry;
	}

	@Override
	public String getRspCode() {
		return rspCode;
	}

	@Override
	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}

	@Override
	public String getRspMsg() {
		return rspMsg;
	}

	@Override
	public void setRspMsg(String rspMsg) {
		this.rspMsg = rspMsg;
	}

	@Override
	public String getParam(String xpath, String defval) {
		String text = getParam(xpath);
		return text == null || "".equals(text) ? defval : text;
	}

	@Override
	public void removeSession(String name) {
		session.removeAttribute(name);
	}

	@Override
	public Map<String, String> findOptionsMap(String itemType) {
		return DictUtils.getValue2LabelMap(itemType);
	}

	@Override
	public <T> T getSessionObject(String name) {
		return (T) session.getAttribute(name);
	}

	@Override
	public void saveSessionObject(String name, Object obj) {
		session.setAttribute(name, obj);
	}

	@Override
	public TransLogBean getTransLogBean(String transCode) {
		if (null == transLogBean) {
			int index = transCode.lastIndexOf("/") + 1;
			transLogBean = new TransLogBean(transCode.substring(index));
		}
		return transLogBean;
	}

	private String chanelType;

	@Override
	public void setChanelType(String chanelType) {
		this.chanelType = chanelType;
	}

	@Override
	public String getChanelType() {
		return this.chanelType;
	}

	private String paramType;

	@Override
	public String getParamType() {
		return paramType;
	}

	@Override
	public void setParamType(String type) {
		this.paramType = type;
	}

	@Override
	public boolean isMapParamType() {
		return PARAM_TYPE_MAP.equals(paramType);
	}

	@Override
	public Map getParamMap() {
		return paraMap;
	}
	
	@Override
	public void setParamMap(Map paraMap) {
		 this.paraMap=paraMap;
	}

	@Override
	public Map<String, Object> getRst(ICommonService commonService, IBusinessContext ctx, String transCode, IParamCover json2MapParamCover,
			IRequstBuilder requestBuilder, IEBankConfParser confParser) {
		Map<String, Object> rst = new HashMap<String, Object>();
		if (!transPrev(commonService, ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst)) {
			return rst;
		}
		return rst;
	}

	/**
	 * 事务前置处理
	 * 
	 * @param commonService
	 * @param ctx
	 * @param transCode
	 * @param json2MapParamCover
	 * @param requestBuilder
	 * @param confParser
	 * @param rst
	 * @return
	 */
	private boolean transPrev(ICommonService commonService, IBusinessContext ctx, String transCode, IParamCover json2MapParamCover,
			IRequstBuilder requestBuilder, IEBankConfParser confParser, Map<String, Object> rst) {
		// 交易开始，设置交易流水
		commonService.generyTransLogSeq(ctx, transCode);
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst);
	}

	@Override
	public void removeParam(String xpath) {
		// TODO Auto-generated method stub
		paraMap.remove(xpath);

	}

	@Override
	public Object findAttribute(String name) {
		if (paraMap.containsKey(name)) {
			return paraMap.get(name);
		} else if (header.containsKey(name)) {
			return header.get(name);

		} else {
			return session.getAttribute(name);
		}
	}

	@Override
	public Map getHeadMap() {
		// TODO Auto-generated method stub
		return header;
	}

}
