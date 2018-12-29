package cn.com.yitong.modules.ares.login.controller;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.framework.util.security.MD5Encrypt;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huyy on 2017/8/17.
 */

@Controller
public class GesturePwdCheck extends BaseControl{
    private Logger logger = YTLog.getLogger(this.getClass());

    @Autowired
    @Qualifier("requestBuilder4db")
    IRequstBuilder requestBuilder;// 请求报文生成器
    @Autowired
    @Qualifier("responseParser4db")
    IResponseParser responseParser;// 响应报文解析器
    @Autowired
    @Qualifier("EBankConfParser4db")
    IEBankConfParser confParser;// 报文装载器
    @Autowired
    ICrudService service;

    final String BASE_PATH = "ares/login/";

    @RequestMapping("ares/login/gesturePwdCheck.do")
    @ResponseBody
    public Map modi(HttpServletRequest request) {
        String trans_code = "GesturePwdCheck";
        String transCode = BASE_PATH + trans_code;
        Map rst = new HashMap();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }

        Map params = ctx.getParamMap();
        Map param = new HashMap();
        try{
            String USER_ID=params.get("USER_ID").toString();
            param.put("USER_ID",USER_ID);
            Map datas=service.load("SYS_USER.CheckPwd", param);
            String pwd=datas.get("USER_PSW").toString();
            String USER_PSW= MD5Encrypt.MD5(params.get("USER_PSW").toString());
            if (pwd.equalsIgnoreCase(USER_PSW)) {
                rst.put("IS_CORRECT", "true");
            }else{
                rst.put("IS_CORRECT", "false");
            }
            transAfter(ctx, transCode, rst, true);
        } catch (Exception e){
            transAfter(ctx, transCode, rst, false);
        }
        return rst;
    }

    /**
     * 事务前置处理
     *
     * @param ctx
     * @param transCode
     * @param rst
     * @return
     */
    private boolean transPrev(IBusinessContext ctx, String transCode, Map rst) {
        // 交易开始，设置交易流水
        commonService.generyTransLogSeq(ctx, transCode);
        return CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
                requestBuilder, confParser, rst);
    }

    /**
     * 事务之后处理
     *
     * @param ctx
     * @param transCode
     * @param rst
     * @param ok
     */
    private void transAfter(IBusinessContext ctx, String transCode, Map rst,
                            boolean ok) {
        // 生成交易结果
        CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
        // 保存交易日志
        commonService.saveJsonTransLog(ctx, transCode, rst);
    }
}
