package cn.com.yitong.util;

import com.alibaba.fastjson.JSONObject;

/**
 * @author luanyu
 * @date   2018年8月27日
 */
public class JsonFormat {
	/**
	 * 格式化json
	 * @param content
	 * @return
	 */
	public static String formatJson(String content) {

		JSONObject json;
		try {
			json = JSONObject.parseObject(content);
		} catch (Exception e1) {
			return content;
		}

		try {
			StringBuffer sb = new StringBuffer();
			int index = 0;
			int count = 0;
			while (index < content.length()) {
				char ch = content.charAt(index);
				if (ch == '{' || ch == '[') {
					sb.append(ch);
					sb.append('\n');
					count++;
					for (int i = 0; i < count; i++) {
						sb.append('\t');
					}
				} else if (ch == '}' || ch == ']') {
					sb.append('\n');
					count--;
					for (int i = 0; i < count; i++) {
						sb.append('\t');
					}
					sb.append(ch);
				} else if (ch == ',') {
					sb.append(ch);
					sb.append('\n');
					for (int i = 0; i < count; i++) {
						sb.append('\t');
					}
				} else {
					sb.append(ch);
				}
				index++;
			}
			return sb.toString();
		} catch (Exception e) {
			return content;
		}
	}

}
