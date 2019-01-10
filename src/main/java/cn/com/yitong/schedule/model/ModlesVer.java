package cn.com.yitong.schedule.model;

/**
 * Created by wangkun on 2015/4/20.
 */
public class ModlesVer {
    /**
     * 功能模块版本编号
     */
    private String srcId;
    /**
     * 模块编号
     */
    private String modId;
    /**
     * 模块版本jar
     */
    private String modJar;
    /**
     * 模块版本路径
     */
    private String modPath;
    /**
     * 当前模块版本
     */
    private String modVerCur;
    /**
     * 模块版本描述
     */
    private String modDes;

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }
    public String getModId() {
        return modId;
    }

    public void setModId(String modId) {
        this.modId = modId;
    }
    public String getModJar() {
        return modJar;
    }

    public void setModJar(String modJar) {
        this.modJar = modJar;
    }
    public String getModPath() {
        return modPath;
    }

    public void setModPath(String modPath) {
        this.modPath = modPath;
    }
    public String getModVerCur() {
        return modVerCur;
    }

    public void setModVerCur(String modVerCur) {
        this.modVerCur = modVerCur;
    }
    public String getModDes() {
        return modDes;
    }

    public void setModDes(String modDes) {
        this.modDes = modDes;
    }

    public static class TF {

        public static String TABLE_NAME = "MODLES_VER";   // 表名

        public static String TABLE_SCHEMA = "MOD";   // 库名

        public static String srcId = "SRC_ID";  // 功能模块版本编号
        public static String modId = "MOD_ID";  // 模块编号
        public static String modJar = "MOD_JAR";  // 模块版本jar
        public static String modPath = "MOD_PATH";  // 模块版本路径
        public static String modVerCur = "MOD_VER_CUR";  // 当前模块版本
        public static String modDes = "MOD_DES";  // 模块版本描述

    }
}
