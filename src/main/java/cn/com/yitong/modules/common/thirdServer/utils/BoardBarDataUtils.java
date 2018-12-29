package cn.com.yitong.modules.common.thirdServer.utils;

import cn.com.yitong.framework.util.CtxUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author zhanglong
 * @date 17/9/20
 */
public class BoardBarDataUtils {

    private static Logger logger = LoggerFactory.getLogger(BoardBarDataUtils.class);

    public static String getBoardData(String transCode) {
        try {
            String resPath = "classpath:META-INF/board/third/" + CtxUtil.transFullPath(transCode) + ".xml";
            File file = ResourceUtils.getFile(resPath);
            String jsonString = FileUtils.readFileToString(file, "utf-8");
            return jsonString;
        }catch (FileNotFoundException e) {
            logger.error("文件未找到：", e);
            return "";
        }catch (IOException e) {
            logger.error("文件读取异常：", e);
            return "";
        }
    }
}
