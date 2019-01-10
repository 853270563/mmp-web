package cn.com.yitong.ares.deviceInfo.controller;

import cn.com.yitong.ares.deviceInfo.service.MdmPolicyItemService;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.StringUtil;
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
 * @author zhanglong@yitong.com.cn
 * @date 15/9/18
 */
@Controller
@SuppressWarnings({"unused", "unchecked"})
public class QueryPolicyItemController extends BaseControl {

    private Logger logger = YTLog.getLogger(this.getClass());

    final String BASE_PATH = "ares/deviceInfo/";

    @Autowired
    @Qualifier("requestBuilder4db")
    IRequstBuilder requestBuilder;// 请求报文生成器
    @Autowired
    @Qualifier("responseParser4db")
    IResponseParser responseParser;// 响应报文解析器
    @Autowired
    @Qualifier("urlClient4db")
    IClientFactory client;// 响应报文解析器
    @Autowired
    @Qualifier("EBankConfParser4db")
    IEBankConfParser confParser;// 报文装载器

    @Autowired
    private MdmPolicyItemService mdmPolicyItemService;

    /**
     * 应用启动时 查询策略信息
     * @param request
     * @return
     */
    @RequestMapping("ares/deviceInfo/queryPolicyItemAppStart.do")
    @ResponseBody
    public Map<String, Object> queryPolicyItemAppStart(HttpServletRequest request) {
        if(logger.isInfoEnabled()) {
            logger.info("查询应用启动时检查策略=================");
        }
        String trans_code = "queryPolicyItemAppStart";
        String transCode = BASE_PATH + trans_code;
        Map rst = new HashMap();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        if (CtxUtil.debugTrans(trans_code)) {
            boolean ok = client.execute(ctx, transCode);
            transAfter(ctx, transCode, rst, ok);
            return rst;
        }
        // 数据库操作区
        Map params = ctx.getParamMap();
        boolean ok = false;
        String message = "";
        try {
            String deviceUuid = (String)params.get("DEVICE_UUID");
            String osSystem = (String)params.get("OS_SYSTEM");
            if(StringUtil.isEmpty(deviceUuid) || StringUtil.isEmpty(osSystem)) {
                message = "请求参数为空，请检查";
            }else {
                rst = mdmPolicyItemService.queryPolicyItemAppStart(deviceUuid, osSystem);
                ok = true;
            }
        } catch (Exception e) {
            // 输出错误的关键信息
            logger.error(ctx.getTransLogBean(transCode), e);
        }
        transAfter(ctx, transCode, rst, ok);
        if(StringUtil.isNotEmpty(message)) {
            rst.put("message", message);
        }
        return rst;
    }

    /**
     * 用户登录时，策略查询
     * @param request
     * @return
     */
    @RequestMapping("ares/deviceInfo/queryPolicyItemLogin.do")
    @ResponseBody
    public Map<String, Object> queryPolicyItemLogin(HttpServletRequest request) {
        if(logger.isInfoEnabled()) {
            logger.info("用户登录时检查策略=================");
        }
        String trans_code = "queryPolicyItemLogin";
        String transCode = BASE_PATH + trans_code;
        Map rst = new HashMap();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        if (CtxUtil.debugTrans(trans_code)) {
            boolean ok = client.execute(ctx, transCode);
            transAfter(ctx, transCode, rst, ok);
            return rst;
        }
        // 数据库操作区
        Map params = ctx.getParamMap();
        boolean ok = false;
        String message = "";
        try {
            String deviceUuid = (String)params.get("DEVICE_UUID");
            String osSystem = (String)params.get("OS_SYSTEM");
            String userId = (String)params.get("USER_ID");
            if(StringUtil.isEmpty(deviceUuid) || StringUtil.isEmpty(osSystem)|| StringUtil.isEmpty(userId)) {
                message = "请求参数为空，请检查";
            }else {
                rst = mdmPolicyItemService.queryPolicyItemLogin(deviceUuid, osSystem, userId);
                ok = true;
            }
        } catch (Exception e) {
            // 输出错误的关键信息
            logger.error(ctx.getTransLogBean(transCode), e);
        }
        transAfter(ctx, transCode, rst, ok);
        if(StringUtil.isNotEmpty(message)) {
            rst.put("message", message);
        }
        return rst;
    }

    /**
     * 违规日志上传
     * @param request
     * @return
     */
    @RequestMapping("ares/deviceInfo/violatePolicyLog.do")
    @ResponseBody
    public Map<String, Object> violatePolicyLog(HttpServletRequest request) {
        if(logger.isInfoEnabled()) {
            logger.info("违规日志上传=================");
        }
        String trans_code = "violatePolicyLog";
        String transCode = BASE_PATH + trans_code;
        Map rst = new HashMap();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        if (CtxUtil.debugTrans(trans_code)) {
            boolean ok = client.execute(ctx, transCode);
            transAfter(ctx, transCode, rst, ok);
            return rst;
        }
        // 数据库操作区
        Map params = ctx.getParamMap();
        boolean ok = false;
        try {
            //数据库操作区
            ok = mdmPolicyItemService.violatePolicyLog(params);
        } catch (Exception e) {
            // 输出错误的关键信息
            logger.error(ctx.getTransLogBean(transCode), e);
        }
        transAfter(ctx, transCode, rst, ok);
        return rst;
    }

    /**
     * 事务前置处理
     * @param ctx
     * @param transCode
     * @param rst
     * @return
     */
    private boolean transPrev(IBusinessContext ctx, String transCode, Map rst) {
        // 交易开始，设置交易流水
        commonService.generyTransLogSeq(ctx, transCode);
        return CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst);
    }

    /**
     * 事务之后处理
     * @param ctx
     * @param transCode
     * @param rst
     * @param ok
     */
    private void transAfter(IBusinessContext ctx, String transCode, Map rst, boolean ok) {
        // 生成交易结果
        CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
        // 保存交易日志
        commonService.saveJsonTransLog(ctx, transCode, rst);
    }
}
