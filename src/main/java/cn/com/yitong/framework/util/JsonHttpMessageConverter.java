package cn.com.yitong.framework.util;

import cn.com.yitong.core.web.convert.CodecJsonHttpMessageConverter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * @author lc3@yitong.com.cn
 */
public class JsonHttpMessageConverter extends CodecJsonHttpMessageConverter {
    /**
     * 是否自动记录挡板数据
     */
    private boolean autoSaveRespData;

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        super.writeInternal(object, outputMessage);
    }

    public boolean isAutoSaveRespData() {
        return autoSaveRespData;
    }

    public void setAutoSaveRespData(boolean autoSaveRespData) {
        this.autoSaveRespData = autoSaveRespData;
    }
}
