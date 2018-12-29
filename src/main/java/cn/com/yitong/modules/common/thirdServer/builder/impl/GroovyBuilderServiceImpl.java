package cn.com.yitong.modules.common.thirdServer.builder.impl;

import cn.com.yitong.modules.common.thirdServer.builder.BuilderParamService;
import cn.com.yitong.modules.common.thirdServer.utils.InvokeGroovyUtils;
import cn.com.yitong.tools.vo.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 使用groovy技术实现xml结构的构造，需要编写groovy脚本，脚本位置为：resource/template/groovy下，
 * 其中builder:子目录为构造xml的脚本，parser:解析xml的脚本
 * 使用时需要传入builderGroovyPath：构建xml的groovy脚本路径，builderMethodName：执行方法，在脚本中定义，builderMap：参数
 * @author zhanglong
 * @date 17/8/30
 */
@Service
public class GroovyBuilderServiceImpl implements BuilderParamService<SimpleResult, Map> {

    private static final Logger logger = LoggerFactory.getLogger(FreeMarkBuilderServiceImpl.class);

    @Override
    public SimpleResult builder(Map param) {
        String groovyPath = (String) param.get("builderGroovyPath");
        String methodName = (String) param.get("builderMethodName");
        Map builderMap = (Map) param.get("builderMap");
        logger.debug("builderMap==={}, groovyPath=={}, methodName=={}", builderMap, groovyPath, methodName);
        Object parser =  InvokeGroovyUtils.invoke(groovyPath, methodName, builderMap);
        if(null == parser) {
            return new SimpleResult(false, "groovy构建xml失败");
        }
        logger.debug("构建后的结果：", parser.toString());
        return new SimpleResult(true, "groovy构建xml成功", parser);
    }
}
