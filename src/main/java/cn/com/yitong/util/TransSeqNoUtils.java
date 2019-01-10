package cn.com.yitong.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransSeqNoUtils {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static String getSeqNoUtilsByDate(int randomLength) {
        return sdf.format(new Date()) + getRandom(randomLength);
    }

    private static String getRandom(int length) {
        int random = new java.util.Random().nextInt();
        return StringUtil.lpadString("" + random, length, "0");
    }
}
