package cn.com.yitong.core.util;

import cn.com.yitong.common.utils.ConfigUtils;

/**
 * 类配置
 * @author lc3@yitong.com.cn
 */
public class ConfigName {

    // 会话相关配置
    public static final String SESSION_TIMEOUT_SECOND = "session.timeout_second";   // 会话超时时间，单位秒
    public static final int SESSION_TIMEOUT_SECOND_DEFVAL = 900;   // 15分钟

    public static final String SESSION_VALIDATION_INTERVAL =
            "session.validation_interval";   // 会话验证时间间隔，单位秒
    public static final int SESSION_VALIDATION_INTERVAL_DEFVAL = 300;   // 5分钟

    public static final String SESSION_TOKEN_LENGTH =
            "session.token_length";   // 会话标识长度
    public static final int SESSION_TOKEN_LENGTH_DEFVAL = 16;   // 16位

    public static final String SESSION_REUSABLE_MAX_LENGTH =
            "session.reusable_max_length";   // 可重复读写的网络请求前多少位
    public static final int SESSION_REUSABLE_MAX_LENGTH_DEFVAL = 128;   // 128位
	public static final String AES_KEY_LENGTH = "aes_key_length"; //AES 加解密长度
	public static final int AES_KEY_LENGTH_DEFAULT = 256; //AES 加解密默认长度

    // 行为日志相关配置
    public static final String BIZ_LOG_BATCH_SERVICE_URL = "log.server_url_batch";  // 行为日志批量提交接口地址
    public static final String BIZ_LOG_SERVICE_URL = "log.server_url";    // 行为日志提交接口地址

    /**
     * @return 数据文件目录
     */
    public static String dataFilesRoot() {
        final String path = ConfigUtils.getValue("upload_files_path");
        if(null != path) {
            return path;
        }
        return System.getProperty("user.home") + "/ares_data_files/";
    }

}
