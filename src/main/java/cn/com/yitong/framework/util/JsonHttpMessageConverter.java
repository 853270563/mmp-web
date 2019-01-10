package cn.com.yitong.framework.util;

import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import cn.com.yitong.core.web.convert.CodecJsonHttpMessageConverter;
import cn.com.yitong.modules.session.service.ExtDemoDataService;

/**
 * @author lc3@yitong.com.cn
 */
public class JsonHttpMessageConverter extends CodecJsonHttpMessageConverter {
    @Resource
    private ExtDemoDataService extDemoDataService;
    /**
     * 是否自动记录挡板数据
     */
    private boolean autoSaveRespData;

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if(autoSaveRespData) {
            extDemoDataService.saveDemoData(object);
        }
        super.writeInternal(object, outputMessage);
    }

    public boolean isAutoSaveRespData() {
        return autoSaveRespData;
    }

    public void setAutoSaveRespData(boolean autoSaveRespData) {
        this.autoSaveRespData = autoSaveRespData;
    }
}
