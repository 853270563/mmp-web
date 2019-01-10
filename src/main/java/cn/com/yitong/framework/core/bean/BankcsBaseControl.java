package cn.com.yitong.framework.core.bean;

import cn.com.yitong.core.base.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;

/**
 * 针对核心接口的基础控制类
 * 
 * @author lc3@yitong.com.cn
 *
 */
public class BankcsBaseControl extends BaseControl {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource(name="requestBuilder4bankcs")
	protected IRequstBuilder requestBuilder;// 请求报文生成器
	@Resource(name="responseParser4bankcs")
	protected IResponseParser responseParser;// 响应报文解析器
	@Resource(name="EBankConfParser4bankcs")
	protected IEBankConfParser confParser;// 报文装载器

    @ExceptionHandler({Exception.class})
    public void exceptionHandler(HttpServletRequest request, HttpServletResponse response,
                                 Exception e) {
        WebUtils.jsonExceptionHandler(response, e);
    }

}
