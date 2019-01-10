package cn.com.yitong.util;

import java.util.Random;

/**
 * 
 * @author iven
 * 
 */
public class RandomUtil {

	private static int[] primes = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37,
			41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107,
			109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179,
			181, 191, 193, 197, 199 };

	/**
	 * 获取步长
	 * 
	 * @return
	 */
	public static int getPrimes() {
		return primes[(int) (Math.random() * (primes.length))];
	}

	/**
	 * 获取基准数
	 * 
	 * @return
	 */
	public static int getBaseNum() {
		return (int) Math.round(Math.random() * 8999 + 1000);
	}

	private static final char[] codeSequences = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * 
	 * 随机生成6位数新密码
	 */
	public static String randomInt(int length) {
		StringBuffer randomCode = new StringBuffer(length);
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String strRand = String.valueOf(codeSequences[random.nextInt(10)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}

	private static final char[] charSequences = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
			'W', 'X', 'Y', 'Z' };

	/**
	 * 
	 * 随机生成6位短信前缀
	 */
	public static String randomString(int length) {
		StringBuffer randomCode = new StringBuffer(length);
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String strRand = String.valueOf(charSequences[random.nextInt(20)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}

	private static final char[] allChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	public static String generateRandom(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar[random.nextInt(allChar.length)]);
		}
		return sb.toString();
	}

	/**
	 * 生产流水号
	 * 
	 * @param session
	 * @return
	 */
	public static String createTransSeq() {
		String random = RandomUtil.randomInt(8);
		return String.format("%d%s", System.currentTimeMillis(), random);

	}

	public static String randomInt(int length, int min, int max) {
		Random random = new Random();
		String strRand = String.format("%0" + length + "X", random.nextInt(max - min) + min);
		if (strRand.length() > length) {
			strRand = strRand.substring(0, length);
		}
		return strRand;
	}

	public static final char[] code16Sequences = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String randomString16(int length) {
		StringBuffer randomCode = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String strRand = String.valueOf(code16Sequences[random.nextInt(16)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}
}
