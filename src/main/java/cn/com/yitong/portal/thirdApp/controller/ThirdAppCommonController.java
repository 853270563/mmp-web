package cn.com.yitong.portal.thirdApp.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.portal.thirdApp.model.AresAuthority;
import cn.com.yitong.portal.thirdApp.service.AresAuthorityService;
import cn.com.yitong.portal.thirdApp.utils.CasSessionUtils;
import cn.com.yitong.core.session.util.SessionManagerUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.core.bean.DBaseControl;
import cn.com.yitong.ares.login.service.LoginService;
import cn.com.yitong.tools.crypto.AESFactory;
import cn.com.yitong.util.StringUtil;

import cn.com.yitong.core.base.WebUtils;

/**
 * @author lc3@yitong.com.cn
 */
@RequestMapping("/portal/thirdApp/")
@Controller
public class ThirdAppCommonController extends DBaseControl {

    public static final String GAIN_ACCESS_TOKEN_URL = "/thirdApp/gainAccessToken.*";
    public static final String FRESH_ACCESS_TOKEN_URL = "/thirdApp/freshAccessToken.*";

    @Resource
    private AresAuthorityService aresAuthorityService;
    @Resource
    private LoginService loginService;

    @RequestMapping("gainAccessToken")
    @ResponseBody
    public Map<String, Object> gainAccessToken(HttpServletRequest request) {
        String transCode = "portal/thirdApp/gainAccessToken";
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }

        String appId = ctx.getParam("appId");
        String appSecret = ctx.getParam("appSecret");

        // 验证密钥
        Map appMap = aresAuthorityService.queryAppInfoByAppId(appId);
        if(null == appMap) {
            return WebUtils.returnErrorMsg(rst, "应用没注册，请在管理平台注册");
        }
        String keyStMd5 = (String)appMap.get("SECRET_KEY");
        String packageName = (String)appMap.get("APP_PACKAGE");
        if(StringUtil.isEmpty(keyStMd5) || StringUtil.isEmpty(packageName)) {
            return WebUtils.returnErrorMsg(rst, "应用秘钥或者包名为空，请在管理平台设置");
        }
        String secret = AESFactory.getInstance().encrypt(packageName, keyStMd5);
        if(!appSecret.equals(secret)) {
            return WebUtils.returnErrorMsg(rst, "当前应用标识和秘钥不匹配");
        }
        AresAuthority authority = CasSessionUtils.getAresAuthority(null);
        if(StringUtil.isEmpty(authority.getAppId()) || !authority.getAppId().equals(appId)) {
            return WebUtils.returnErrorMsg(rst, "授权许可码和当前应用不匹配");
        }

        String rs = aresAuthorityService.genToken(authority);
        if(null != rs) {
            return WebUtils.returnErrorMsg(rst, rs);
        }

        rst.put("accessToken", authority.getAccessToken());
        rst.put("freshToken", authority.getFreshToken());

        return WebUtils.returnSuccessMsg(rst, "");
    }

    /**
     * 刷新访问令牌
     * @param request 请求
     * @return
     */
    @RequestMapping("freshAccessToken")
    @ResponseBody
    public Map<String, Object> freshAccessToken(HttpServletRequest request) {
        String transCode = "portal/thirdApp/freshAccessToken";
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }

        String accessToken = ctx.getParam("accessToken");

        AresAuthority authority = CasSessionUtils.getAresAuthority(null);
        if(!authority.getAccessToken().equals(accessToken)) {
            return WebUtils.returnErrorMsg(rst, "访问令牌和刷新令牌不一致");
        }

        authority = aresAuthorityService.refreshToken();

        rst.put("accessToken", authority.getAccessToken());

        // 刷新门户会话时间
        SessionManagerUtils.getDefaultManager().getOrCreateSession(authority.getPortalSessionId());

        return WebUtils.returnSuccessMsg(rst, "");
    }

    @RequestMapping("checkAccessToken")
    @ResponseBody
    public Map<String, Object> checkAccessToken(HttpServletRequest request) {
        String transCode = "portal/thirdApp/checkAccessToken";
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }

        AresAuthority authority = CasSessionUtils.getAresAuthority(null);

        String userId = authority.getUserId();
        Map<String, Object> user = loginService.loadUserById(userId);
        if(null == user) {
            return WebUtils.returnErrorMsg(rst, "当前用户不存在");
        }

        rst.put("effective", "1");
        rst.put("userId", userId);
        rst.put("userName", user.get("NAME_CN"));
        rst.put("role", "thirdApp");

        return WebUtils.returnSuccessMsg(rst, "");
    }
}
