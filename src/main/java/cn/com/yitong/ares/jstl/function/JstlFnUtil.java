/**
 * 
 */
package cn.com.yitong.ares.jstl.function;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO EL表达式内部使用的字符串函数
 * 
 * @作者：yym
 * @邮箱：yym@yitong.com.cn
 * @创建时间：2016年9月14日 下午7:19:52
 * @版本信息：
 */
public class JstlFnUtil {

	private static JstlFnUtil instance = new JstlFnUtil();

	public static JstlFnUtil getInstance() {
		return instance;
	}

	Map m = new HashMap();

	public Map initFunction() {
		if (!m.isEmpty()) {
			return m;
		}
		try {
			Class c = JstlFnUtil.class;
			m.put("fn:contains", c.getMethod("contains", new Class[] { String.class, String.class }));
			m.put("fn:concat", c.getMethod("concat", new Class[] { String.class, String.class }));
			m.put("fn:indexOf", c.getMethod("indexOf", new Class[] { String.class, String.class }));
			m.put("fn:startWith", c.getMethod("startWith", new Class[] { String.class, String.class }));
			m.put("fn:endWith", c.getMethod("endWith", new Class[] { String.class, String.class }));
			m.put("fn:length", c.getMethod("length", new Class[] { String.class }));
			m.put("fn:substringAfter", c.getMethod("substringAfter", new Class[] { String.class, String.class }));
			m.put("fn:substringBefore", c.getMethod("substringBefore", new Class[] { String.class, String.class }));
			m.put("fn:trim", c.getMethod("trim", new Class[] { String.class }));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return m;
	}

	public static boolean contains(String source, String searchText) {
		return source.contains(searchText);
	}

	/**
	 * 连接字符串
	 * 
	 * @param source
	 * @param appendText
	 * @return
	 */
	public static String concat(String source, String appendText) {
		return source.concat(appendText);
	}

	public static int indexOf(String source, String text) {
		return source.indexOf(text);
	}

	public static boolean startWith(String source, String prefix) {
		return source.startsWith(prefix);
	}

	public static boolean endWith(String source, String suffix) {
		return source.endsWith(suffix);
	}

	public static int length(String source) {
		return source.length();
	}

	public static String substringAfter(String source, String text) {
		int index = source.indexOf(text);
		return source.substring(index + text.length());
	}

	public static String substringBefore(String source, String text) {
		int index = source.indexOf(text);
		return source.substring(0, index);
	}

	public static String trim(String source) {
		return source.trim();
	}

	public static String replace(String source, String regex, String replacement) {
		return source.replaceAll(regex, replacement);
	}
}
