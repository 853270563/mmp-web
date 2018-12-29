package cn.com.yitong.modules.common.thirdServer.utils;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhanglong
 * @date 17/5/10
 */
public class InvokeGroovyUtils {

    private static Logger logger = LoggerFactory.getLogger(InvokeGroovyUtils.class);

    @SuppressWarnings("rawtypes")
    public static Object invoke(String groovyPath, String methodName, Object xml) {
        logger.info("开始调用groovy执行=====groovyPath==" + groovyPath + ", methodName==" + methodName);
        GroovyClassLoader loader = new GroovyClassLoader();
        try {
            Class groovyClass = loader.parseClass(new File(InvokeGroovyUtils.class.getResource(groovyPath).toURI()));
            GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
            Object obj = groovyObject.invokeMethod(methodName, xml);
            logger.info("处理成功=============================");
            return obj;
        } catch (Exception e) {
            logger.info("处理失败=============================", e);
            return null;
        }
    }
}
