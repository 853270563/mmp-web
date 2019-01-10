package cn.com.yitong.util;
public class F_R_K {

	public static String f_out = "";

	public String SI(String s) {

		String si = "";
		String a_s, b_s, temp;
		int a_i, b_i, c;
		for (int i = 0; i < 48; i += 6) {

			a_s = s.substring(i, i + 1) + s.substring(i + 5, i + 6);
			a_i = Integer.parseInt(a_s, 2);

			b_s = s.substring(i + 1, i + 5);
			b_i = Integer.parseInt(b_s, 2);

			c = ShareData.S[i / 6][a_i][b_i];

			temp = Integer.toBinaryString(c);
			for (int j = 4 - temp.length(); j > 0; j--)
				temp = "0" + temp;
			si += temp;
		}
		return si;
	}

	public F_R_K(String s, String k) {

		String re = ShareData.Conversion(s, ShareData.E);
		String k_re = ShareData.XOR(re, k);
		String si = SI(k_re);
		f_out = ShareData.Conversion(si, ShareData.P);
	}
}