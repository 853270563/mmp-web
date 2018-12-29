package cn.com.yitong.modules.common.thirdServer.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanglong
 * @date 17/8/29
 */
public class FreeMarkTplCacheUtils {

    private static final Logger logger = LoggerFactory.getLogger(FreeMarkTplCacheUtils.class);

    //缓存机制
    private static Map<String, Template> tran2Tpl = new HashMap<String, Template>();

    /**
     * 获取当前交易的参数模板
     * @param transModel 交易模块
     * @param transCode 交易名
     */
    public static Template getTpl(String transModel, String transCode) throws Exception {
        String trnasPath = transModel + "/" + transCode + ".ftl";
        try {
            if (tran2Tpl.containsKey(trnasPath)) {
                return tran2Tpl.get(trnasPath);
            } else {
                Configuration cfg = new Configuration();
                cfg.setDirectoryForTemplateLoading(getTplPath(transModel));
                cfg.setDefaultEncoding("UTF-8");
                Template tpl = cfg.getTemplate(transCode + ".ftl");
                tran2Tpl.put(trnasPath, tpl);
                return tpl;
            }
        }catch (IOException e) {
            logger.error("获取FreeMark模板失败：", e);
            return null;
        }
    }

    /**
     * 获取交易模块对应的模板目录
     */
    private static File getTplPath(String transModel) throws Exception {
        return new File(FreeMarkTplCacheUtils.class.getResource("/template/freemarktpl/" + transModel).toURI());
    }
}
