package cn.com.yitong.portal.thirdApp.controller;

import cn.com.yitong.portal.thirdApp.model.AresAuthority;
import cn.com.yitong.portal.thirdApp.service.AresAuthorityService;
import cn.com.yitong.core.base.WebUtils;
import cn.com.yitong.consts.NS;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.core.bean.DBaseControl;
import cn.com.yitong.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 门户通用接口控制器
 * @author lc3@yitong.com.cn
 */
@RequestMapping("/portal/thirdApp/")
@Controller
public class PortalCommonController extends DBaseControl {

    @Resource
    private AresAuthorityService aresAuthorityService;

    @RequestMapping("gainAuthCode")
    @ResponseBody
    public Map<String, Object> gainAuthCode(HttpServletRequest request) {
        String transCode = "portal/thirdApp/gainAuthCode";
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }

        String appId = ctx.getParam("appId");
        Map appMap = aresAuthorityService.queryAppInfoByAppId(appId);
        if(null == appMap) {
            return WebUtils.returnErrorMsg(rst, "应用没注册，请在管理平台注册");
        }

        String appStatus = (String)appMap.get("APP_STATUS");
        if(StringUtil.isEmpty(appStatus) || NS.APP_STATUS_DOWN.equals(appStatus)) {
            return WebUtils.returnSuccessMsg(rst, "应用没上架，请在管理平台设置");
        }

        AresAuthority authority = aresAuthorityService.create(appId);
        aresAuthorityService.save(authority);

        rst.put("authCode", authority.getAuthCode());
        rst.put("secretKey", authority.getSecretKeyWithNoCodec());
        return WebUtils.returnSuccessMsg(rst, "交易成功");
    }
}
