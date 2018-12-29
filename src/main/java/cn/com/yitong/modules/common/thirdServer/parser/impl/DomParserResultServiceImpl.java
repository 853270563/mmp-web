package cn.com.yitong.modules.common.thirdServer.parser.impl;

import cn.com.yitong.modules.common.thirdServer.parser.ParserResultService;
import cn.com.yitong.modules.common.thirdServer.utils.Dom4jParserXmlUtils;
import cn.com.yitong.tools.vo.SimpleResult;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用dom4j来解析返回的xml报文，不同项目组报文格式不相同，根据具体情况可修改或者重新实现
 * @author zhanglong
 * @date 17/8/30
 */
@Service
public class DomParserResultServiceImpl implements ParserResultService<SimpleResult, Map> {

    private static final Logger logger = LoggerFactory.getLogger(DomParserResultServiceImpl.class);

    @Override
    public SimpleResult parser(Map result) {
        String resposeXml = (String) result.get("responseResult");
        logger.debug("resposeXml==={}", resposeXml);

        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Document doc = DocumentHelper.parseText(resposeXml.replace("&gt;", ">").replace("&lt;", "<")
                    .replace("&quot;", "\"").replaceAll("<\\?xml[^>]*?>", "").replace("<![CDATA[", "").replace("]]>", ""));
            Element root = doc.getRootElement();
            Map<String, Object> headerMap = Dom4jParserXmlUtils.parserElment(root.element("Header").element("HeadType"), null);
            resultMap.put("header", headerMap);
            Map<String, Object> bodyMap = Dom4jParserXmlUtils.parserElment(root.element("Body").element("NoAS400"), "name");
            resultMap.put("body", bodyMap);
        }catch (Exception e) {
            logger.error("使用dom4j解析xml异常：", e);
            new SimpleResult(false, "使用dom4j解析xml异常" + e.getMessage());
        }
        return new SimpleResult(true, "dom4j解析xml成功", resultMap);
    }
}
