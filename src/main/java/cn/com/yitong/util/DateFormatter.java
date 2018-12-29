package cn.com.yitong.util;

public interface DateFormatter {

    String[] DATE_PATTERNS = new String[] { "yyyy-MM-dd" };

    String[] CONVERT_DATE_PATTERNS = new String[] { "yyyy-MM-dd", "yyyy/MM/dd", "yyyy年MM月dd日", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd hh:mm:ss" };

    String SDF_YMD1 = "yyyy-MM-dd";

    String SDF_YMD2 = "yyyy/MM/dd";

    String SDF_YMD3 = "yyyy年MM月dd日";

    String SDF_YMDHMS1 = "yyyy-MM-dd HH:mm:ss";

    String SDF_YMDHMS2 = "yyyy-MM-dd hh:mm:ss";
}
