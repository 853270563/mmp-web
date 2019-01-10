package cn.com.yitong.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class CharsetUtil {

	private final static CharsetEncoder GBK = Charset.forName("gb2312")
			.newEncoder();
	private final static CharsetEncoder BIG5 = Charset.forName("BIG5")
			.newEncoder();


	/**
	 * 判断是否GBK编码
	 * 
	 * @return
	 */
	public static boolean isCnZHString(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		char[] chs = str.toCharArray();
		for (int i = 0; i < chs.length; i++) {
			if (!GBK.canEncode(chs[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否全部能被BIG5 编码的字符串
	 * 
	 * @return
	 */
	public static boolean isCnHKString(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		char[] chs = str.toCharArray();
		for (int i = 0; i < chs.length; i++) {
			if (!BIG5.canEncode(chs[i])) {
				return false;
			}
		}
		return true;
	}
}