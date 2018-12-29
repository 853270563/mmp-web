package cn.com.yitong.modules.common.thirdServer.parser.impl;

import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.modules.common.thirdServer.parser.ParserResultService;
import cn.com.yitong.modules.common.thirdServer.utils.InvokeGroovyUtils;
import cn.com.yitong.tools.vo.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author zhanglong
 * @date 17/8/29
 */
@Service
public class GroovyParserResultServiceImpl implements ParserResultService<SimpleResult, Map> {

    private static final Logger logger = LoggerFactory.getLogger(GroovyParserResultServiceImpl.class);

    @Override
    public SimpleResult parser(Map result) {
        String parserGroovyPath = (String) result.get("parserGroovyPath");
        String parserMethodName = (String) result.get("parserMethodName");
        String resposeXml = (String) result.get("responseResult");
        logger.debug("resposeXml==={}, parserGroovyPath=={}, parserMethodName=={}", resposeXml, parserGroovyPath, parserMethodName);
        Object parser = InvokeGroovyUtils.invoke(parserGroovyPath, parserMethodName, resposeXml);
        if(null == parser) {
            return new SimpleResult(false, "groovy解析xml失败");
        }
        if(parser instanceof Map) {
            return new SimpleResult(true, "groovy解析xml成功", parser);
        }
        String resultJson = JsonUtils.objectToJson(parser);
        Map resultMap = JsonUtils.jsonToMap(resultJson);
        logger.debug("解析后的结果：", resultMap);
        return new SimpleResult(true, "groovy解析xml成功", resultMap);
    }
}
