package cn.com.yitong.framework.core.bean;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.util.CtxUtil;
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

import java.util.Map;

/**
 * 针对数据库的基础控制类
 * 
 * @author lc3@yitong.com.cn
 *
 */
public class DBaseControl extends BaseControl {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource(name="requestBuilder4db")
	protected IRequstBuilder requestBuilder;// 请求报文生成器
	@Resource(name="responseParser4db")
	protected IResponseParser responseParser;// 响应报文解析器
	@Resource(name="EBankConfParser4db")
	protected IEBankConfParser confParser;// 报文装载器

    /**
     * 检查报文定义
     * @param ctx 总线
     * @param transCode 交易码
     * @param rst 返回数据
     * @return
     */
    protected boolean transPrev(IBusinessContext ctx, String transCode, Map<String, Object> rst) {
        // 交易开始，设置交易流水
        commonService.generyTransLogSeq(ctx, transCode);
        return CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst);
    }

    @ExceptionHandler({Exception.class})
    public void exceptionHandler(HttpServletRequest request, HttpServletResponse response,
                                 Exception e) {
        WebUtils.jsonExceptionHandler(response, e);
    }

}
