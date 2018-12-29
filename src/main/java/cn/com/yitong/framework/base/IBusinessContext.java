package cn.com.yitong.framework.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.dom4j.Element;

import cn.com.yitong.framework.core.vo.TransLogBean;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IParamCover;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.service.ICommonService;

/**
 * 数据总线接口，采用Element为数据结构
 * 
 * @author yaoym
 * 
 */
public interface IBusinessContext {

	public static final String PARAM_TYPE_XML = "xml";
	public static final String PARAM_TYPE_MAP = "map";

	public static final String RET_OK = "ok";
	public static final String RET_ERROR = "error";

	/**
	 * 获取参数上下文类型
	 * 
	 * @return
	 */
	public String getParamType();

	public boolean isMapParamType();

	/**
	 * 获取Map容器的客户端请求参数集
	 * 
	 * @return
	 */
	public Map getParamMap();
	
	public void setParamMap(Map paraMap);

	/**
	 * 设置参数上下文类型
	 * 
	 * @param type
	 */
	public void setParamType(String type);

	public boolean initParamCover(boolean signed);

	/**
	 * 请求解析
	 * 
	 * @param paramCover
	 * @param signed
	 *            是否解密
	 * @return
	 */
	public boolean initParamCover(IParamCover paramCover, boolean signed);

	/**
	 * 请求解析
	 * 
	 * @param paramCover
	 * @param transCode
	 * @param signed
	 *            是否解密
	 * @return
	 */
	public boolean initParamCover(IParamCover paramCover, String transCode, boolean signed);

	/**
	 * 获取某交易的专属请求数据区
	 * 
	 * @param transCode
	 * @return
	 */
	public Element getRequestContext(String transCode);

	/**
	 * 保存账号到Session
	 * 
	 * @param name
	 *            value
	 * @return
	 */
	public void saveSessionList(String name, List<Map> value);

	/**
	 * 获取某交易的专属响应数据区
	 * 
	 * @param transCode
	 * @return
	 */
	public Element getResponseContext(String transCode);

	// 获取请求数据
	public Element getRequestContext();

	// 获取响应数据
	public Element getResponseContext();

	// 获取响应状态
	public String getResponseStatus();

	/**
	 * 数据总线根节点
	 * 
	 * @return
	 */
	public Element getBaseContext();

	public HttpSession getHttpSession();

	public void setErrorInfo(String errCode, String errMsg, String transCode);

	public void setSuccessInfo(String sucCode, String sucMsg, String transCode);

	/**
	 * 可保证数据总线中只有一个该实例
	 * 
	 * @param parent
	 * @param name
	 * @param text
	 * @return
	 */
	public Element saveNode(Element parent, String name, String text);

	/**
	 * 
	 * 在数据总线新增实例，超过一个实例时将变成数组
	 * 
	 * @param parent
	 * @param name
	 * @param text
	 * @return
	 */
	public Element addNode(Element parent, String name, String text);

	/**
	 * 获取请求的参数
	 * 
	 * @param xpath
	 * @return
	 */
	public String getParam(String xpath);

	/**
	 * 获取请求的参数，参数为空时，使用缺省值
	 * 
	 * @param xpath
	 * @param defval
	 * @return
	 */
	public String getParam(String xpath, String defval);

	/**
	 * 设置请求参数
	 * 
	 * @param name
	 * @param text
	 * @return
	 */
	public boolean setParam(String name, String text);

	/**
	 * 获取请求的参数对象数组
	 * 
	 * @param xpath
	 * @return
	 */
	public List getParamDatas(String xpath);

	/**
	 * 获取字符串会话
	 * 
	 * @param xpth
	 * @return
	 */
	public String getSessionText(String name);

	/**
	 * 获取会话对象
	 * 
	 * @param name
	 * @return
	 */
	public <T> T getSessionObject(String name);

	/**
	 * 保存会话对象
	 * 
	 * @param name
	 * @param obj
	 */
	public void saveSessionObject(String name, Object obj);

	/**
	 * 消除会活属性
	 * 
	 * @param name
	 */
	public void removeSession(String name);

	/**
	 * 保存对象到会话中
	 * 
	 * @param name
	 * @param elem
	 * @return
	 */
	public boolean saveSession(IMBElement elem);

	/**
	 * 保存对象到会话中
	 * 
	 * @param name
	 * @param elem
	 * @return
	 */
	public List<Map> getSessionList(String name);

	/**
	 * 保存文本会话
	 * 
	 * @param name
	 * @param value
	 */
	public void saveSessionText(String name, String value);

	/**
	 * 获取会话对象
	 * 
	 * @param name
	 * @return
	 */
	public Element getSessionElement(String name);

	public String toString();

	/**
	 * 获取客户端请求参数
	 * 
	 * @return
	 */
	Element getParamContext();

	/**
	 * 数据字典解析
	 * 
	 * @param key
	 */
	public Map<String, String> findOptionsMap(String key);

	Object getResponseEntry();

	void setRequestEntry(Object requestEntry);

	Object getRequestEntry();

	void setResponseEntry(Object responseEntry);

	String getRspCode();

	void setRspCode(String rspCode);

	String getRspMsg();

	void setRspMsg(String rspMsg);

	/**
	 * 获取基础交易日志对象
	 * 
	 * @param transCode
	 * @return
	 */
	public TransLogBean getTransLogBean(String transCode);

	/**
	 * 设置渠道类型
	 * 
	 * @param chanelJson
	 */
	public void setChanelType(String chanelType);

	/**
	 * 获取渠道类型
	 * 
	 * @return
	 */
	public String getChanelType();

	/**
	 * 获取Rst
	 * 
	 * @return
	 */
	public Map<String, Object> getRst(ICommonService commonService, IBusinessContext ctx, String transCode, IParamCover json2MapParamCover,
									  IRequstBuilder requestBuilder, IEBankConfParser confParser);

}
