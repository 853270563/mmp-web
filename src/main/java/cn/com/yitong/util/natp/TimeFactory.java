package cn.com.yitong.util.natp;

import java.text.SimpleDateFormat;

public class TimeFactory {
    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日 HH:mm:ss:SS");
        java.util.Date dateTime = new java.util.Date();
        String time = dateFormat.format(dateTime);
        return time;
    }

    /**
     * 返回流水号 格式YYYYMMDDHHMMSSXXXXXX
     * 
     * @return
     * @author: 贺雅飞
     */
    public static String getSerial() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date dateTime = new java.util.Date();
        String p_Serial = dateFormat.format(dateTime);
        String A_Serial = "00";
        return p_Serial + A_Serial;

    }

}
