package cn.com.yitong.modules.common.thirdServer.exec.webservice;

import cn.com.yitong.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhanglong
 * @date 17/4/21
 */
public class InvokeCredit {

    private static final Logger logger = LoggerFactory.getLogger(InvokeCredit.class);

    public String invokeHttpWebservice(String interfaceUrl, String inParamXml, Integer soTimeOut, String action) throws Exception {
        if(StringUtil.isEmpty(interfaceUrl) || StringUtil.isEmpty(inParamXml)) {
            logger.error("请求地址为空或者请求参数为空");
            return "";
        }
        long start = System.currentTimeMillis();
        SoapClient client = new SoapClient(HttpClientFactory.createHttpClient(soTimeOut, soTimeOut));


        String soapResponseMsg = client.sendRequest(interfaceUrl, inParamXml, action);
        logger.info("soapResponseMsg:" + soapResponseMsg);
        logger.info("接口调用时间：" + (System.currentTimeMillis()-start));
        return soapResponseMsg;
    }
}
