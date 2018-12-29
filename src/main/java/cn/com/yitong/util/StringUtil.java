package cn.com.yitong.util;

import cn.com.yitong.core.util.DictUtils;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Clob;
import java.util.*;

public class StringUtil {

	/**
	 * 根据交易号生成IBS系统的跳转页面路径
	 */
	public static String generyForwardPath(String tranCode) {
		if (StringUtil.isEmpty(tranCode)) {
			return "error";
		} else if (tranCode.startsWith("P", 1)) {
			return tranCode.substring(2, 4) + "/" + tranCode;
		}
		return tranCode;
	}

	public static String iso2utf8(String src) {
		try {
			if (isEmpty(src)) {
				return "";
			}
			return new String(src.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			return "?";
		}
	}

	public static String iso2gbk(String src) {
		try {
			if (isEmpty(src)) {
				return "";
			}
			return new String(src.getBytes("iso-8859-1"), "gbk");
		} catch (UnsupportedEncodingException e) {
			return "?";
		}
	}

	/**
	 * <li>判断字符串是否为空值</li> <li>NULL、空格均认为空值</li>
	 */
	public static boolean isEmpty(String value) {
		return null == value || "".equals(value.trim());
	}

	/**
	 * <li>判断字符串是否为空值</li> <li>NULL、空格均认为空值</li>
	 */
	public static boolean isEmpty(Object value) {
		return null == value || "".equals(value.toString().trim());
	}

	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 内容不为空
	 */
	public static boolean isNotEmpty(String value) {
		return null != value && !"".equals(value.trim());
	}

	/**
	 * 重复字符串 如 repeatString("a",3) ==> "aaa"
	 */
	public static String repeatString(String src, int repeats) {
		if (null == src || repeats <= 0) {
			return src;
		} else {
			StringBuffer bf = new StringBuffer();
			for (int i = 0; i < repeats; i++) {
				bf.append(src);
			}
			return bf.toString();
		}
	}

	/**
	 * 左对齐字符串 * lpadString("X",3); ==>" X" *
	 */
	public static String lpadString(String src, int length) {
		return lpadString(src, length, " ");
	}

	/**
	 * 以指定字符串填补空位，左对齐字符串 * lpadString("X",3,"0"); ==>"00X"
	 */
	public static String lpadString(String src, int length, String single) {
		if (src == null || length <= src.getBytes().length) {
			return src;
		} else {
			return repeatString(single, length - src.getBytes().length) + src;
		}
	}

	/**
	 * 右对齐字符串 * rpadString("9",3)==>"9 "
	 */
	public static String rpadString(String src, int byteLength) {
		return rpadString(src, byteLength, " ");
	}

	/**
	 * 以指定字符串填补空位，右对齐字符串 rpadString("9",3,"0")==>"900"
	 */
	public static String rpadString(String src, int length, String single) {
		if (src == null || length <= src.getBytes().length) {
			return src;
		} else {
			return src + repeatString(single, length - src.getBytes().length);
		}
	}

	/**
	 * 去除,分隔符，用于金额数值去格式化
	 */
	public static String decimal(String value) {
		if (null == value || "".equals(value.trim())) {
			return "0";
		} else {
			return value.replaceAll(",", "");
		}
	}

	/**
	 * 在数组中查找字符串
	 * 
	 * @param params
	 * @param name
	 * @param ignoreCase
	 * @return
	 */
	public static int indexOf(String[] params, String name, boolean ignoreCase) {
		if (params == null)
			return -1;
		for (int i = 0, j = params.length; i < j; i++) {
			if (ignoreCase && params[i].equalsIgnoreCase(name)) {
				return i;
			} else if (params[i].equals(name)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 将字符转数组
	 * 
	 * @param str
	 * @return
	 */
	public static String[] toArr(String str) {
		String inStr = str;
		String a[] = null;
		try {
			if (null != inStr) {
				StringTokenizer st = new StringTokenizer(inStr, ",");
				if (st.countTokens() > 0) {
					a = new String[st.countTokens()];
					int i = 0;
					while (st.hasMoreTokens()) {
						a[i++] = st.nextToken();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;
	}

	/**
	 * 字符串数组包装成字符串
	 * 
	 * @param ary
	 * @param s
	 *            包装符号如 ' 或 "
	 * @return
	 */
	public static String toStr(String[] ary, String s) {
		if (ary == null || ary.length < 1)
			return "";
		StringBuffer bf = new StringBuffer();
		bf.append(s);
		bf.append(ary[0]);
		for (int i = 1; i < ary.length; i++) {
			bf.append(s).append(",").append(s);
			bf.append(ary[i]);
		}
		bf.append(s);
		return bf.toString();
	}

	/**
	 * 設置MESSAGE中的變量{0}...{N}
	 * 
	 * @param msg
	 * @param vars
	 * @return
	 */
	public static String getMessage(String msg, String[] vars) {
		for (int i = 0; i < vars.length; i++) {
			msg = msg.replaceAll("\\{" + i + "\\}", vars[i]);
		}
		return msg;
	}

	/**
	 * @param msg
	 * @param var
	 * @return
	 */
	public static String getMessage(String msg, String var) {
		return getMessage(msg, new String[] { var });
	}

	/**
	 * @param msg
	 * @param var
	 * @param var2
	 * @return
	 */
	public static String getMessage(String msg, String var, String var2) {
		return getMessage(msg, new String[] { var, var2 });
	}

	/**
	 * 取整数值
	 * 
	 * @param map
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static int getInt(Map map, String key, int defValue) {
		if (null != map && isNotEmpty(key)) {
			try {
				return Integer.parseInt(String.valueOf(map.get(key)));
			} catch (Exception e) {
			}
		}
		return defValue;
	}

	/**
	 * 取浮点值
	 * 
	 * @param map
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static float getFloat(Map map, String key, int defValue) {
		if (null != map && isNotEmpty(key)) {
			try {
				return Float.parseFloat(map.get(key).toString());
			} catch (Exception e) {
			}
		}
		return defValue;
	}

	/**
	 * 取字符串
	 * 
	 * @param map
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Map map, String key, String defValue) {
		if (null != map && isNotEmpty(key)) {
			try {
				return (String) map.get(key);
			} catch (Exception e) {
			}
		}
		return defValue;
	}

	/**
	 * 获取XML节点文本
	 * 
	 * @param elem
	 * @param xpath
	 * @return
	 */
	public static String getElementText(Element elem, String xpath) {
		return getElementText(elem, xpath, "");
	}

	/**
	 * 获取XML节点文本
	 * 
	 * @param elem
	 * @param xpath
	 * @return
	 */
	public static String getElementText(Element elem, String xpath,
			String defValue) {
		Node node = elem.selectSingleNode(xpath);
		return null == node ? defValue : node.getText();
	}

	public static String appLikeCondition(String elem, boolean preFlag,
			boolean nextFlag) {
		StringBuffer bf = new StringBuffer();
		if (preFlag) {
			bf.append("%");
		}
		if (isNotEmpty(elem)) {
			bf.append(elem);
		}
		if (nextFlag) {
			bf.append("%");
		}
		return bf.toString();
	}

	/**
	 * 將key=value;key2=value2……轉換成Map
	 * 
	 * @param params
	 * @return
	 */
	public static Map gerneryParams(String params) {
		Map args = new HashMap();
		if (!isEmpty(params)) {
			try {
				String map, key, value;
				StringTokenizer st = new StringTokenizer(params, ";");
				StringTokenizer stMap;
				while (st.hasMoreTokens()) {
					map = st.nextToken();
					if (isEmpty(map.trim()))
						break;
					stMap = new StringTokenizer(map, "=");
					key = stMap.hasMoreTokens() ? stMap.nextToken() : "";
					if (isEmpty(key.trim()))
						continue;
					value = stMap.hasMoreTokens() ? stMap.nextToken() : "";
					args.put(key, value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return args;
	}

	/**
	 * 页面格式化日期:yyyyMMdd ---> yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(String date) {
		return isEmpty(date) ? "" : DateUtil.format(date, "yyyyMMdd",
				"yyyy-MM-dd");
	}

	/**
	 * 解析文件的扩展名
	 * 
	 * @param oldName
	 * @return
	 */
	public static String getFileExtName(String oldName) {
		String ext = "";
		int lastIndex = oldName.lastIndexOf(".");
		if (lastIndex > 0) {
			ext = oldName.substring(lastIndex);
		}
		return ext;
	}

	public static void generyXmlEntry(StringBuffer bf, String entry,
			Object value) {
		bf.append("<").append(entry).append(">");
		if (null != value)
			bf.append(value.toString().trim());
		bf.append("</").append(entry).append(">");
	}

	public static void generyXmlEntryCData(StringBuffer bf, String entry,
			Object value) {
		bf.append("<").append(entry).append("><![CDATA[");
		if (null != value)
			bf.append(value);
		bf.append("]]></").append(entry).append(">");
	}

	/**
	 * 生成图片访问全路径
	 * 
	 * @param rootUrl
	 *            图片服务器根目录
	 * @param dir
	 *            分类目录
	 * @param imgId
	 *            图片ID
	 * @param imgType
	 *            图片类型
	 * @return
	 */
	public static String generyImgUrl(Object rootUrl, Object dir, Object imgId,
			Object imgType) {
		StringBuffer bf = new StringBuffer();
		try {
			bf.append(rootUrl).append("/");
			bf.append(dir).append("/");
			bf.append(imgId).append(".").append(imgType);
		} catch (Exception e) {
			bf.append("");
		}
		return bf.toString();
	}

	public static boolean isNumber(String s) {
		return s.matches("[0-9\\.]+");
	}

	public static int string2Int(String s) {
		if (s == null || "".equals(s) || "undefined".equals(s))
			return 0;
		int result = 0;
		try {
			if (s.indexOf(",") != -1) {
				s = s.substring(0, s.indexOf(","));
			} else if (s.indexOf(".") != -1) {
				s = s.substring(0, s.indexOf("."));
			} else if (s.indexOf("|") != -1) {
				s = s.substring(0, s.indexOf("|"));
			}
			if (isNumber(s)) {
				result = Integer.parseInt(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean string2boolean(String s) {
		return "true".equals(s);
	}

	public static String formatAccount(String acctNo, String format) {
		if ("#### ####".equals(format)) {
			if (isNotEmpty(acctNo) && acctNo.length() > 4) {
				StringBuffer bf = new StringBuffer();
				int max = acctNo.length() - 4;
				int i = 0;
				while (i < max) {
					bf.append(acctNo.substring(i, i + 4)).append(" ");
					i += 4;
				}
				bf.append(acctNo.substring(i));
				return bf.toString();
			}
		}
		return acctNo;
	}

	/**
	 * 格式化xml数据
	 * 
	 * @param stringWriter
	 * @param elem
	 * @throws java.io.IOException
	 */
	public static void formateXMLStr(Writer stringWriter, Element elem)
			throws IOException {
		OutputFormat of = new OutputFormat();
		of.setIndent(true);
		of.setNewlines(true);
		XMLWriter xmlWriter = new XMLWriter(stringWriter, of);
		xmlWriter.write(elem);
		xmlWriter.close();
	}

	/**
	 * 格式化xml数据
	 * 
	 * @param xmlStr
	 * @return
	 */
	public static String formatXmlStr(String xmlStr) {
		return formatXmlStr(xmlStr, "utf-8");
	}

	/**
	 * 格式化xml数据
	 * 
	 * @param xmlStr
	 * @param encoding
	 * @throws java.io.IOException
	 */
	public static String formatXmlStr(String xmlStr, String encoding) {
		Document doc;
		try {
			doc = DocumentHelper.parseText(xmlStr);
		} catch (DocumentException e) {
			e.printStackTrace();
			return xmlStr;
		}
		doc.setXMLEncoding(encoding);
		Writer stringWriter = new StringWriter();
		OutputFormat of = new OutputFormat();
		of.setIndent(true);
		of.setNewlines(true);
		XMLWriter xmlWriter = new XMLWriter(stringWriter, of);
		try {
			xmlWriter.write(doc);
			xmlWriter.close();
		} catch (IOException e) {

			e.printStackTrace();
			return xmlStr;
		}
		return stringWriter.toString();
	}

	public static String replacePwd(String log) {
		log = log.replaceAll("\\\"Password\":\".*?\"", "\"Password\":\"\"");
		log = log.replaceAll("\\\"FundTransPwd\":\".*?\"",
				"\"FundTransPwd\":\"\"");
		log = log.replaceAll("\\\"MobilePasswd\":\".*?\"",
				"\"MobilePasswd\":\"\"");
		log = log.replaceAll("\\\"Passwd\":\".*?\"", "\"Passwd\":\"\"");
		return log;
	}

	public static String replacePwdFormXMl(String log) {
		log = log.replaceAll("<Password>.*?</Password>",
				"<Password></Password>");
		log = log.replaceAll("<FundTransPwd>.*?</FundTransPwd>",
				"<FundTransPwd></FundTransPwd>");
		log = log.replaceAll("<MobilePasswd>.*?</MobilePasswd>",
				"<MobilePasswd></MobilePasswd>");
		return log;
	}

	public static String replaceXMLPwd(String log) {
		log = log.replaceAll("<Password>.*?</Password>",
				"<Password></Password>");
		log = log.replaceAll("<TrsPassword>.*?</TrsPassword>",
				"<TrsPassword></TrsPassword>");
		log = log.replaceAll("<TrsPwd>.*?</TrsPwd>", "<TrsPwd></TrsPwd>");
		log = log.replaceAll("<OldPassword>.*?</OldPassword>",
				"<OldPassword></OldPassword>");
		log = log.replaceAll("<AcountPassword>.*?</AcountPassword>",
				"<AcountPassword></AcountPassword>");
		return log;
	}

	/**
	 * 将XML的最近节点转成MAP
	 * 
	 * @param parent
	 * @return
	 */
	public static Map xmlToMap(final Element parent) {
		Map map = new HashMap();
		if (parent == null)
			return map;
		List<Element> elems = parent.elements();
		if (elems == null)
			return map;
		for (Element ele : elems) {
			if (ele == null)
				continue;
			map.put(ele.getName(), ele.getText().trim());
		}
		return map;
	}

	/**
	 * 从xml中提取map要素
	 * 
	 * @param elem
	 * @param keys
	 * @return
	 */
	public static Map xmlToMap(final Element elem, final String[] keys) {
		return xmlToMap(elem, keys, keys);
	}

	/**
	 * 从xml中提取map要素
	 * 
	 * @param elem
	 * @param xpaths
	 * @param keys
	 * @return
	 */
	public static Map xmlToMap(final Element elem, final String[] xpaths,
			final String[] keys) {
		if (elem == null || xpaths == null || keys == null
				|| xpaths.length != keys.length) {
			return null;
		}
		Map map = new HashMap();
		for (int i = 0; i < xpaths.length; i++) {
			Node node = elem.selectSingleNode(xpaths[i]);
			if (node == null)
				continue;
			map.put(keys[i], node.getText().trim());
		}
		return map;
	}

	/**
	 * Map到Element
	 * 
	 * @param elem
	 * @param xpaths
	 * @param keys
	 * @param map
	 * @return
	 */
	public static boolean mapToXml(Element elem, final String[] xpaths,
			final String[] keys, final Map map) {
		if (map == null || elem == null || xpaths == null || keys == null
				|| xpaths.length != keys.length) {
			return true;
		}
		for (int i = 0; i < keys.length; i++) {
			Object text = map.get(keys[i]);
			elem.addElement(xpaths[i]).setText(
					text == null ? "" : text.toString());
		}
		return true;
	}

	/**
	 * Map to Map
	 * 
	 * @param map
	 * @param srcKeys
	 * @param targetKeys
	 * @return
	 */
	public static Map mapToMap(Map map, final String[] srcKeys,
			final String[] targetKeys) {
		if (srcKeys == null || targetKeys == null
				|| srcKeys.length != targetKeys.length) {
			return null;
		}
		Map target = new HashMap();
		for (int i = 0, j = srcKeys.length; i < j; i++) {
			target.put(targetKeys[i], map.get(srcKeys[i]));
		}
		return target;
	}

	/**
	 * List<Map>考备
	 * 
	 * @param srcList
	 * @param srcKeys
	 * @param destList
	 * @param destKeys
	 * @return
	 */
	public static boolean copyMapList(List<Map> srcList,
			final String[] srcKeys, List<Map> destList, final String[] destKeys) {
		for (Map src : srcList) {
			destList.add(mapToMap(src, srcKeys, destKeys));
		}
		return true;
	}

	/**
	 * 转整数
	 * 
	 * @param str
	 * @return
	 */
	public static int parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 
	 * @param str
	 * @param def
	 * @return
	 */
	public static int parseInt(String str, int def) {
		try {
			return parseInt(str);
		} catch (Exception e) {
			return def;
		}
	}

	/**
	 * 拷贝Map到Element，xpaths作为标签名，map中的value作为值
	 * 
	 * @return true copy success,else false
	 */
	public static boolean copyMapToElement(Element elem, final String[] xpaths,
			final String[] keys, final Map map) {
		if (map == null || elem == null || xpaths == null || keys == null
				|| xpaths.length != keys.length) {
			return false;
		}
		for (int i = 0; i < keys.length; i++) {
			Object text = map.get(keys[i]);
			elem.addElement(xpaths[i]).setText(
					text == null ? "" : text.toString());
		}
		return true;
	}

	/**
	 * 当前日期
	 * 
	 * @return
	 */
	public static String today() {
		return DateUtil.todayStr();
	}

	/**
	 * 当前日期
	 * 
	 * @param format
	 *            格式化
	 * @return
	 */
	public static String todayStr(String format) {
		return DateUtil.todayStr(format);
	}

	/**
	 * 提前N天的日期
	 * 
	 * @param curDate
	 * @param days
	 * @return
	 */
	public static String beforeDate(Date curDate, int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(curDate);
		c.add(Calendar.DAY_OF_YEAR, -days);
		return DateUtil.formatDateToStr(c.getTime(), DateUtil.DATE_FORMATTER);

	}

	/**
	 * 显示文本
	 * 
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static String showValue(String value, String defaultValue) {
		if (isEmpty(value)) {
			if (isNotEmpty(defaultValue)) {
				return defaultValue;
			} else {
				return "";
			}
		}
		return value;
	}

	/**
	 * 提取N月前的日期
	 * 
	 * @param days
	 * @return
	 */
	public static String beforeMonth(Integer days) {
		return DateUtil.beforeMonthDate(DateUtil.todayStr(), days);
	}

	/**
	 * 指定日期的前N月日期
	 * 
	 * @param dateStr
	 * @param days
	 * @return
	 */
	public static String beforeNMonth(String dateStr, Integer days) {
		return DateUtil.beforeMonthDate(dateStr, days);
	}

	/**
	 * 提取N天前的日期
	 * 
	 * @param days
	 * @return
	 */
	public static String beforeNDate(Integer days) {
		return beforeDate(DateUtil.today(), days);
	}

	public static String beforeNDate1(String date, Integer days) {
		return beforeDate(DateUtil.parseDate(date), days);
	}

	public static String beforeDate() {
		return beforeDate(DateUtil.today(), 7);
	}

	public static String getServerPath(Object obj) {
		String path = obj.getClass().getProtectionDomain().getCodeSource()
				.getLocation().getPath();
		if (path.indexOf("WEB-INF") > 0) {
			path = path.substring(1, path.indexOf("WEB-INF"));
		}
		return path;
	}

	public static void main(String[] args) {
		String longStr = "123456789";
		List<String> shortStrs = fixedLengthIntercept(longStr, 9);
		for(String ss : shortStrs) {
			System.out.printf("ss===" + ss + "\n");
		}
	}

	/**
	 * 更改大豐銀行的 10 位 12位 16位的帐号到17位 存储的时候用
	 * */
	public static String unFormatAcct(String accStr) {
		String newAcc = "";
		if (accStr != null) {
			accStr = accStr.trim().replace("-", "");
		}
		if (accStr != null) {
			if (accStr.trim().length() == 10) {
				newAcc = "000000" + accStr.substring(3, 4)
						+ accStr.substring(1, 3) + accStr.substring(0, 1) + "0"
						+ accStr.substring(4);
			} else if (accStr.trim().length() == 12) {
				newAcc = "00000" + accStr;
			} else if (accStr.trim().length() == 14) {
				newAcc = "000" + accStr;
			} else if (accStr.trim().length() == 16) {
				newAcc = "0" + accStr;
			} else {
				newAcc = accStr;
			}
			return newAcc;
		} else {
			return accStr;
		}
	}

	/**
	 * 格式化大豐銀行的帳號17,14位到 12位 10位
	 * */
	public static String formatAcct(String accStr) {

		if (accStr != null) {
			accStr = accStr.trim();
			if (accStr.length() == 17) {
				accStr = accStr.substring(3);
			}
			if (accStr.length() == 14) {
				if ("0".equals(accStr.substring(7, 8))) {
					// 老帳號顯示10位的 000-0-00000-0,以下是規則
					accStr = accStr.substring(6, 7) + accStr.substring(4, 6)
							+ "-" + accStr.substring(3, 4) + "-"
							+ accStr.substring(8, 13) + "-"
							+ accStr.substring(13);
				} else { // 新帳號的顯示14位的 00-00-00-0-000000-0
					accStr = accStr.substring(0, 2) + "-"
							+ accStr.substring(2, 4) + "-"
							+ accStr.substring(4, 6) + "-"
							+ accStr.substring(6, 7) + "-"
							+ accStr.substring(7, 13) + "-"
							+ accStr.substring(13);
					if ("00-".equals(accStr.substring(0, 3))) { // 12位的顯示方式
						accStr = accStr.substring(3);
					}
				}

			}
		}
		return accStr;
	}

	/**
	 * 显示参数文本
	 * 
	 * @param key
	 * @param itemType
	 * @param language
	 * @return
	 */
	public static String showLabel(String key, String itemType, String language) {
		return DictUtils.getDictLabel(itemType, key, key);
	}
	
	/**
	 * 显示参数文本，用默认语言
	 * @param key
	 * @param itemType
	 * @return
	 */
	public static String showLabel(String key, String itemType) {
		return showLabel(key, itemType, "");
	}

	/**
	 * 获取请求IP地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("http_client_ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		// 如果是多级代理，那么取第一个ip为客户ip
		if (ip != null && ip.indexOf(",") != -1) {
			ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
		}
		return ip;
	}

	/**
	 * 字符截取字符串
	 * 
	 * @param orignal
	 * @param subcount
	 * @return
	 */
	public static String subStringByByte(String orignal, int subcount) {
		int reInt = 0;
		String reStr = "";
		if (orignal == null)
			return "";
		char[] tempChar = orignal.toCharArray();
		for (int kk = 0; (kk < tempChar.length && subcount > reInt); kk++) {
			String s1 = orignal.valueOf(tempChar[kk]);
			byte[] b = s1.getBytes();
			reInt += b.length;
			reStr += tempChar[kk];
		}
		return reStr;
	}

	/**
	 * 把字符串按照固定长度截取成 集合
	 * @param str 字符串
	 * @param len 长度
	 * @return
	 */
	public static List<String> fixedLengthIntercept(String str, int len) {
		List<String> retStrList = new ArrayList<String>();
		if(StringUtil.isEmpty(str)) {
			return retStrList;
		}
		String shortStr = "";
		while (str.length() > len) {
			shortStr = str.substring(0, len);
			retStrList.add(shortStr);
			str = str.substring(len);
		}
		retStrList.add(str);
		return retStrList;
	}

	public static String capitalizeAll(String str) {
		if (null == str || str.isEmpty()) {
			return str;
		}
		StringBuilder s = new StringBuilder(str.length());
		boolean isUpper = true;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if ('_' == c) {
				isUpper = true;
				continue;
			}
			if (isUpper) {
				s.append(Character.toUpperCase(c));
				isUpper = false;
			} else {
				s.append(Character.toLowerCase(c));
			}
		}
		return s.toString();
	}

	public static String uncapitalize(final String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		char firstChar = str.charAt(0);
		if (Character.isLowerCase(firstChar)) {
			// already uncapitalized
			return str;
		}
		return new StringBuilder(strLen)
				.append(Character.toLowerCase(firstChar))
				.append(str.substring(1))
				.toString();
	}

	/**
	 *
	 * Description:将Clob对象转换为String对象,Blob处理方式与此相同
	 *
	 * @param clob
	 * @return
	 */
	public static String oracleClob2Str(Clob clob) throws Exception {
		if (clob == null || clob.length() <= 0){
			return "";
		}
		return clob.getSubString(1, (int) clob.length());
	}
	
	/**
	 * 获取32位UUID
	 * @return
	 */
	public static String uuid(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * obj ==> "" or string
	 * @return
	 */
	public static String obj2String(Object obj){
		return null == obj?"":obj.toString();
	}
}
