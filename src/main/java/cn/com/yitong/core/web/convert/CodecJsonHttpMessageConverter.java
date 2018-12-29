package cn.com.yitong.core.web.convert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.core.web.util.LoginCodecUtils;
import cn.com.yitong.tools.codec.CodecSupport;

/**
 * 进行ARES加解密对应的JsonHttpMessageConverter
 * @author lc3@yitong.com.cn
 */
public class CodecJsonHttpMessageConverter extends MappingJacksonHttpMessageConverter {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public CodecJsonHttpMessageConverter() {
        super();
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if(!SecurityUtils.canCodec()) {
            super.writeInternal(object, outputMessage);
            return;
        }
        if(SecurityUtils.isLogining() && object instanceof Map) {
            LoginCodecUtils.returnLoginData((Map<String, Object>) object);
        }
        MockHttpOutputMessageWrapper httpOutputMessage = new MockHttpOutputMessageWrapper(outputMessage);
        super.writeInternal(object, httpOutputMessage);
        String outMsg = httpOutputMessage.getBodyAsString();
        try {
            outMsg = SecurityUtils.encode(outMsg);

            OutputStream outputStream = outputMessage.getBody();
            outputStream.write(CodecSupport.toBytes(outMsg));
            outputStream.flush();
        } catch (Exception e) {
            outMsg = "返回报文加密异常，请联系管理员";
            if(null != e.getMessage()) {
                outMsg += "，异常原因为：" + e.getMessage();
            }
            if(logger.isWarnEnabled()) {
                logger.warn(outMsg, e);
            }
            super.writeInternal(returnErrorMsg(outMsg, e), outputMessage);
        }
    }

    /**
     * 生成返回错误信息，必要时可以重写
     * @param errorMsg 错误信息
     * @param e 异常
     * @return
     */
    protected Map<String, Object> returnErrorMsg(String errorMsg, Throwable e) {
        Map<String, Object> rtn = new HashMap<String, Object>(2);
        rtn.put("STATUS", "0");
        rtn.put("MSG", errorMsg);
        return rtn;
    }

    private static class MockHttpOutputMessageWrapper implements HttpOutputMessage {

        private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
        private final ByteArrayOutputStream body = new ByteArrayOutputStream();
        private HttpOutputMessage outputMessage = null;

        public MockHttpOutputMessageWrapper(HttpOutputMessage outputMessage) {
            this.outputMessage = outputMessage;
        }

        @Override
        public HttpHeaders getHeaders() {
            return outputMessage.getHeaders();
        }

        public OutputStream getBody() throws IOException {
            return this.body;
        }

        public byte[] getBodyAsBytes() {
            return this.body.toByteArray();
        }

        public String getBodyAsString() {
            return this.getBodyAsString(DEFAULT_CHARSET);
        }

        public String getBodyAsString(Charset charset) {
            byte[] bytes = this.getBodyAsBytes();

            try {
                return new String(bytes, charset.name());
            } catch (UnsupportedEncodingException e) {
                throw new InternalError(e.getMessage());
            }
        }
    }
}
