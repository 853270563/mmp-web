package cn.com.yitong.util.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class StringUtil { 

	public static String iso2utf8(String src) {
		try {
			if (isEmpty(src))
				return "";
			return new String(src.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			return "?";
		}
	}

	public static String iso2gbk(String src) {
		try {
			if (isEmpty(src))
				return "";
			return new String(src.getBytes("iso-8859-1"), "gbk");
		} catch (UnsupportedEncodingException e) {
			return "?";
		}
	}

	public static String utf2gbk(String src) {
		try {
			if (isEmpty(src))
				return "";
			return new String(src.getBytes("utf-8"), "gbk");
		} catch (UnsupportedEncodingException e) {
			return "?";
		}
	}

	public static String gbk2utf(String src) {
		try {
			if (isEmpty(src))
				return "";
			return new String(src.getBytes("gbk"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			return "?";
		}
	}

	public static String gbk2utf(byte[] gbk) {
		try {
			return new String(gbk, "gbk");
		} catch (UnsupportedEncodingException e) {
			return "?";
		}
	}

	/**
	 * <li>判断字符串是否为空值</li> <li>NULL、空格均认为空值</li>
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value) {
		return null == value || "".equals(value.trim()) || "null".equals(value);
	}

	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0)
			return true;
		for (int i = 0; i < strLen; i++)
			if (!Character.isWhitespace(str.charAt(i)))
				return false;

		return true;
	}

	/**
	 * 内容不为空
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNotEmpty(String value) {
		return null != value && !"".equals(value.trim());
	}

	/**
	 * 重复字符串 如 repeatString("a",3) ==> "aaa"
	 * 
	 * @author uke
	 * @param src
	 * @param repeats
	 * @return
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
	 * 
	 * @param src
	 * @param length
	 * @return
	 */
	public static String lpadString(String src, int length) {
		return lpadString(src, length, " ");
	}

	/**
	 * 以指定字符串填补空位，左对齐字符串 * lpadString("X",3,"0"); ==>"00X"
	 * 
	 * @param src
	 * @param byteLength
	 * @param temp
	 * @return
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
	 * 
	 * @param src
	 * @param byteLength
	 * @return
	 */
	public static String rpadString(String src, int byteLength) {
		return rpadString(src, byteLength, " ");
	}

	/**
	 * 以指定字符串填补空位，右对齐字符串 rpadString("9",3,"0")==>"900"
	 * 
	 * @param src
	 * @param byteLength
	 * @param single
	 * @return
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
	 * 
	 * @param value
	 * @return
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
				return Integer.parseInt((String) map.get(key));
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

	public static boolean isNumber(String s) {
		if (s == null)
			return false;
		return s.matches("[0-9\\.]+");
	}

	/**
	 * 转整数
	 * 
	 * @param str
	 * @return
	 */
	public static int parseInt(String str) {
		if (!isNumber(str))
			return 0;
		return Integer.parseInt(str);
	}

	/**
	 * 
	 * @param str
	 * @param def
	 * @return
	 */
	public static int parseInt(String str, int def) {
		int rst = parseInt(str);
		if (rst < 0) {
			return def;
		}
		return rst;
	}

	public static void generyXmlEntry(StringBuffer bf, String entry, String value) {
		bf.append(String.format("<%s>%s</%s>", entry, value, entry));
	}

	/**
	 * 从Map中取String类型值
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	public static Object getMapValue(Map map, Object key) {
		if (null == map || null == key)
			return "";

		if ((key instanceof String)) {
			String keystr = (String) key;
			keystr = keystr.toUpperCase();
			key = keystr;
		}
		Object value = map.get(key);
		return null == value ? "" : value;
	}

	public static void generyXmlEntryCData(StringBuffer bf, String entry, Object value) {
		bf.append("<").append(entry).append("><![CDATA[");
		if (null != value)
			bf.append(value);
		bf.append("]]></").append(entry).append(">");
	}

	public static String generyImgUrl(Object rootUrl, Object date, Object imgId, Object imgInfo) {
		StringBuffer bf = new StringBuffer();
		try {
			String ext = StringUtil.getFileExtName((String) imgInfo);
			bf.append(rootUrl).append("/");
			bf.append(date).append("/");
			bf.append(imgId).append(ext);
		} catch (Exception e) {
			bf.append("");
		}
		return bf.toString();
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

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}
   
	public static int string2Int(String str) {
		return parseInt(str);
	}

	/**
	 * 格式化xml数据
	 * 
	 * @param stringWriter
	 * @param doc
	 * @throws IOException
	 */
	public static void formateXMLStr(Writer stringWriter, Document doc) throws IOException {
		XMLWriter xmlWriter = null;
		try {
			OutputFormat of = new OutputFormat();
			of.setIndent(true);
			of.setEncoding("UTF-8");
			of.setNewlines(true);
			xmlWriter = new XMLWriter(stringWriter, of);
			xmlWriter.write(doc);
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != xmlWriter) {
				xmlWriter.close();
			}
		}
	}

	/**
	 * replaceXmlPwd 屏蔽密码输出
	 * 
	 * @param log
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	public static String replaceXmlPwd(String log) {
		String repLog = log;
		try {
			repLog = repLog.replaceAll("<PASSWORD>.*?</PASSWORD>", "<PASSWORD>******</PASSWORD>");
			repLog = repLog.replaceAll("<password>.*?</password>", "<password>******</password>");
			repLog = repLog.replaceAll("<Passwd>.*?</Passwd>", "<Passwd>******</Passwd>");
		} catch (Exception e) {
			e.printStackTrace();
			return log;
		}
		return repLog;
	}

	/**
	 * replaceJsonPwd 屏蔽密码输出
	 * 
	 * @param sendData
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	public static String replaceJsonPwd(String log) {
		String repLog = log;
		try {
			repLog = repLog.replaceAll("\\\"PASSWORD\\\":\\\".*?\\\"", "\"PASSWORD\":\"\"");
			repLog = repLog.replaceAll("\\\"password\\\":\\\".*?\"", "\"password\":\"\"");
			repLog = repLog.replaceAll("\\\"Passwd\":\\\".*?\\\"", "\"Passwd\":\"\"");
		} catch (Exception e) {
			e.printStackTrace();
			return log;
		}
		return repLog;
	}

	/**
	 * 
	 * StringUtil.stripToEmpty(null) = "" StringUtil.stripToEmpty("") = "" StringUtil.stripToEmpty("   ") = ""
	 * StringUtil.stripToEmpty("abc") = "abc" StringUtil.stripToEmpty("  abc") = "abc" StringUtil.stripToEmpty("abc  ")
	 * = "abc" StringUtil.stripToEmpty(" abc ") = "abc" StringUtil.stripToEmpty(" ab c ") = "ab c"
	 * 
	 * @param value
	 * @return
	 */
	public static String stripToEmpty(Object value) {

		if (null == value) {
			return "";
		}
		String str = String.valueOf(value);
		if (StringUtils.isBlank(str)) {
			return "";
		}
		return StringUtils.stripToEmpty(str);
	}

	/**
	 * @param map
	 * @param key
	 * @param def
	 * @return
	 */
	public static String getString(Map map, String key, String def) {
		if (null != map && isNotEmpty(key)) {
			String value = (String) map.get(key);
			return isEmpty(value) ? def : value;
		}
		return def;
	}
	
	/**
     * 带参字符串文本
     * 
     * @param temp
     * @param params
     * @return
     */
	public static String message(String temp, Object... params) {
		for (int i = 0; i < params.length; i++) {
			temp = temp.replaceAll("\\{" + i + "\\}", params[i].toString());
		}
		return temp;
	}
}
