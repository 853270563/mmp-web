package cn.com.yitong.market.mxd.util;

/**
 * Created by QianJH on 2015-7-17.
 */
public class Utils {
    public static class TASK_STATUS {
        /**
         * 待申请
         */
        public static final String STATUS_01 = "01";

        /**
         * 待查询
         */
        public static final String STATUS_02 = "02";

        /**
         * 待审批
         */
        public static final String STATUS_03 = "03";

        /**
         * 已审批
         */
        public static final String STATUS_04 = "04";

        /**
         * 已拒绝
         */
        public static final String STATUS_05 = "05";

        /**
         * 已完成
         */
        public static final String STATUS_06 = "06";
    }

    public static final String XW_ZXCX_FILE_ATTR_TYPE = "XW_ZXCX_FILE_ATTR_TYPE";  //小微征信查询提交的文件
    public static final String XW_DKDC_FILE_ATTR_TYPE = "XW_DKDC_FILE_ATTR_TYPE";  //小微贷款调查提交的文件

    public static final String OK = "OK";


    public static String getFileTypeByFileName(String fileName) {
        if (null == fileName || fileName.isEmpty()) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}
