package cn.com.yitong.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求参数工具类
 * 
 * @author uke
 * 
 */
public class ParamUtil {

	/**
	 * <li>判断字符串是否為空</li> <li>NULL,空格字符返回真</li>
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value) {
		return null == value || "".equals(value.trim());
	}

	/**
	 * <li>用於處理JS參數解碼,配合WEB页面二次转码</li> <li>
	 * JS：params="&name="+encodeURI(encodeURI("姓名"));</li> <li>
	 * JAVA：ParamUtil.decode(request,"name")=="姓名"</li>
	 * 
	 * @param request
	 * @param param
	 * @return
	 * @throws java.io.UnsupportedEncodingException
	 */
	public static String decode(HttpServletRequest request, String param)
			throws UnsupportedEncodingException {
		String value = request.getParameter(param);
		return java.net.URLDecoder.decode(value, "UTF-8");
	}

	/**
	 * <li>獲取Request中的參數，並且去除NULL</li>
	 * 
	 * @param request
	 * @param param
	 * @return
	 */
	public static String getChn(HttpServletRequest request, String param) {
		return getChn(request, param, "");
	}

	/**
	 * ISO中文
	 * 
	 * @param request
	 * @param param
	 * @return
	 */
	public static String getIsoChn(HttpServletRequest request, String param) {
		String value = request.getParameter(param);
		try {
			return new String(value.getBytes("iso-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return value;
		}
	}

	/**
	 * <li>獲取Request中的參數，並且去除NULL,以指定内容取代</li>
	 * 
	 * @param request
	 * @param param
	 * @param defValue
	 * @return
	 */
	public static String getChn(HttpServletRequest request, String param,
			String defValue) {
		String value = null;
		try {
			value = decode(request, param);
		} catch (Exception e) {
			e.printStackTrace();
			value = request.getParameter(param);
		}
		return null == value ? defValue : value;
	}

	/**
	 * <li>獲取Request中的參數，並且去除NULL</li>
	 * 
	 * @param request
	 * @param param
	 * @return
	 */
	public static String get(HttpServletRequest request, String param) {
		return get(request, param, "");
	}

	/**
	 * <li>獲取Request中的參數，並且去除NULL,以指定内容取代</li>
	 * 
	 * @param request
	 * @param param
	 * @param defValue
	 * @return
	 */
	public static String get(HttpServletRequest request, String param,
			String defValue) {
		String value = request.getParameter(param);
		return null == value ? defValue : value.trim();
	}

	/**
	 * 整型值
	 * 
	 * @param request
	 * @param param
	 * @param defValue
	 * @return
	 */
	public static int getInt(HttpServletRequest request, String param,
			int defValue) {
		String value = request.getParameter(param);
		if (!isEmpty(value)) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
			}
		}
		return defValue;
	}

	/**
	 * 布爾值
	 * 
	 * @param request
	 * @param param
	 * @return
	 */
	public static boolean getBoolean(HttpServletRequest request, String param) {
		return "true".equals(get(request, param));
	}

	/**
	 * 金額類型
	 * 
	 * @param request
	 * @param param
	 * @return
	 */
	public static Double getDouble(HttpServletRequest request, String param) {
		try {
			String value = get(request, param, "0").replaceAll(",", "");
			return new Double(value);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 日期類型
	 * 
	 * @param request
	 * @param param
	 * @return
	 */
	public static Date getDate(HttpServletRequest request, String param) {
		return DateUtil.parseDate(get(request, param));
	}

	/**
	 * 日期類型字符串yyyyMMdd
	 * 
	 * @param request
	 * @param param
	 * @return
	 */
	public static String getDateStr(HttpServletRequest request, String param) {
		return get(request, param).replaceAll("-|\\/", "");
	}

	/**
	 * 向从Request中获取字符串值到Map中,包含空值
	 * 
	 * @param request
	 * @param param
	 * @param params
	 */
	public static void putStr2Map(HttpServletRequest request, String param,
			Map paramMap) {
		paramMap.put(param, get(request, param));
	}

	/**
	 * 向从Request中获取整型值到Map中，不包含空值
	 * 
	 * @param request
	 * @param param
	 * @param map
	 */
	public static void putInt2Map(HttpServletRequest request, String param,
			Map paramMap) {
		paramMap.put(param, new Integer(getInt(request, param, 0)));
	}

	/**
	 * 向从Request中获取浮點值到Map中，不包含空值
	 * 
	 * @param request
	 * @param param
	 * @param map
	 */
	public static void putDouble2Map(HttpServletRequest request, String param,
			Map paramMap) {
		paramMap.put(param, getDouble(request, param));
	}

	/**
	 * 向从Request中将日期值yyyy-MM-dd转成yyyyMMdd到Map中，不包含空值
	 * 
	 * @param request
	 * @param param
	 * @param map
	 */
	public static void putDateStr2Map(HttpServletRequest request, String param,
			Map paramMap) {
		String value = getDateStr(request, param);
		if (!isEmpty(value)) {
			paramMap.put(param, value);
		}
	}

	private static String[] forbids = { "action" };

	private static boolean checkParams(String name) {
		for (int i = 0, j = forbids.length; i < j; i++) {
			if (forbids[i].equals(name))
				return false;
		}
		return true;
	}

	public static String generyHiddenInput(String name, String value) {
		String html = "<input type='hidden' name='0' value=\"1\">";
		html = html.replaceFirst("0", name);
		html = html.replaceFirst("1", value);
		return html;
	}

	/**
	 * 转换参数
	 * 
	 * @param request
	 * @param prefix
	 * @param forbid
	 * @return
	 */
	public static String fixParamToHtml(HttpServletRequest request,
			String prefix, boolean forbid) {
		StringBuffer bf = new StringBuffer();
		Enumeration names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			String value = get(request, name);
			if (!isEmpty(value)) {
				if (forbid) {
					if (!checkParams(name)) {
						continue;
					}
				}
				bf.append(generyHiddenInput(prefix + name, value));
			}
		}
		return bf.toString();
	}

	/**
	 * 转换参数
	 * 
	 * @param request
	 * @param prefix
	 * @param forbid
	 * @return
	 */
	public static String fixParamToHtml(HttpServletRequest request,
			String prefix) {
		return fixParamToHtml(request, prefix, true);
	}

	/**
	 * <ul>
	 * 保留請求中的參數，生成限制参数之外的参数对
	 * <li>forbids[]内设定的参数名不会生成参数对</li>
	 * </ul>
	 * 
	 * @param request
	 * @param prefix
	 * @param forbids
	 * @return
	 */
	public static String forbidFixParamToHtml(HttpServletRequest request,
			String prefix, String[] forbids) {
		StringBuffer bf = new StringBuffer();
		Enumeration names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			String value = get(request, name);
			// 過濾參數對
			if (!isEmpty(value) && StringUtil.indexOf(forbids, name, false) < 0) {
				bf.append(generyHiddenInput(prefix + name, value));
			}
		}
		return bf.toString();
	}

	public static String generyFixParamToHtml(HttpServletRequest request,
			String prefix) {
		StringBuffer bf = new StringBuffer();
		Enumeration names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			if (name.startsWith(prefix)) {
				try {
					String value = get(request, name);
					if (!isEmpty(value)) {
						bf.append(generyHiddenInput(name, value));
					}
				} catch (Exception e) {
				}
			}
		}
		return bf.toString();
	}

	/**
	 * 将指定前缀的参数的前缀去除，并生成隐含HTML对象
	 * 
	 * @param request
	 * @param prefix
	 * @return
	 */
	public static String decodeParamToHtml(HttpServletRequest request,
			String prefix) {
		StringBuffer bf = new StringBuffer();
		Enumeration names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			if (name.startsWith(prefix) && !"_backUrl".equals(name)) {
				String value = get(request, name);
				name = name.substring(prefix.length());
				if (!isEmpty(value)) {
					bf.append(generyHiddenInput(name, value));
				}
			}
		}
		return bf.toString();
	}

	/**
	 * 将裝飾過的HttpServletRequest參數分离出来，存储到MAP中 <li>request: _name ==> map:_name</li>
	 * 
	 * @param request
	 * @param prefix
	 * @return
	 */
	public static Map enfixParam(HttpServletRequest request, String prefix) {
		Enumeration names = request.getParameterNames();
		Map params = new HashMap();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			if (name.startsWith(prefix)) {
				String value = get(request, name);
				if (!isEmpty(value)) {
					params.put(name, value);
				}
			}
		}
		return params;
	}

	/**
	 * <li>还原被装饰过的HttpServletRequest參數</li> <li>去除装饰，并将还原后的参数存储到MAP中</li> <li>
	 * request: _name ==> map:name</li>
	 * 
	 * @param request
	 * @param prefix
	 * @return
	 */
	public static Map defixParam(HttpServletRequest request, String prefix) {
		Enumeration names = request.getParameterNames();
		Map params = new HashMap();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			if (name.startsWith(prefix)) {
				String value = get(request, name);
				name = name.substring(prefix.length());
				if (!isEmpty(value)) {
					params.put(name, value);
				}
			}
		}
		return params;
	}

	/**
	 * 解析请求，并将参数值对以key:value字符串保存
	 * 
	 * @param request
	 * @return
	 */
	public static String favorit(HttpServletRequest request) {
		return favorit(request, true);
	}

	/**
	 * 保存有效页面参数
	 * 
	 * @param request
	 * @param forbid
	 * @return
	 */
	public static String favorit(HttpServletRequest request, boolean forbid) {
		String url = request.getRequestURL().toString();
		String ctx = request.getContextPath();
		int index = url.indexOf(ctx);
		String name, value;
		StringBuffer bf = new StringBuffer();
		bf.append("URL:'").append(url.substring(index + ctx.length() + 1))
				.append("'");
		Enumeration names = request.getParameterNames();
		while (names.hasMoreElements()) {
			name = (String) names.nextElement();
			if (forbid && name.startsWith("_"))
				continue;
			value = get(request, name);
			if (!(null == value || "".equals(value))) {
				bf.append(",").append(name).append(":\"").append(value)
						.append("\"");
			}
		}
		return bf.toString();
	}

	public static Map<String, String> formatObjectMapToStringMap(Map<String, Object> params) {
		Map<String,String> resultMap=new HashMap<String,String>();
		if(params==null){
			return null;
		}
		Iterator<String> it=params.keySet().iterator();
		while(it.hasNext()){
			String key=it.next();
			if(params.get(key) instanceof String){
				resultMap.put(key, (String)params.get(key));
			}
		}
		return resultMap;
	}
}
