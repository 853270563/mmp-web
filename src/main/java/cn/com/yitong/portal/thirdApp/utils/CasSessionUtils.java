package cn.com.yitong.portal.thirdApp.utils;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.util.UrlPathHelper;

import cn.com.yitong.portal.thirdApp.controller.ThirdAppCommonController;
import cn.com.yitong.portal.thirdApp.model.AresAuthority;
import cn.com.yitong.portal.thirdApp.service.AresAuthorityService;
import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.common.utils.StringUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.SessionException;
import cn.com.yitong.core.session.util.SessionManagerUtils;
import cn.com.yitong.core.session.vo.SimpleSession;
import cn.com.yitong.core.session.vo.ValidateSession;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.core.util.ThreadContext;
import cn.com.yitong.util.ConfigName;

/**
 * 统一会话工具类
 * @author lc3@yitong.com.cn
 */
public class CasSessionUtils {

    public static final String CAS_SESSION_ARES_AUTHORITY = "cas_session_ares_authority";
    public static final String HTTP_HEADER_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String HTTP_HEADER_FRESH_TOKEN = "FRESH_TOKEN";
    public static final String HTTP_HEADER_AUTH_CODE = "AUTH_CODE";
    private static final UrlPathHelper urlPathHelper = new UrlPathHelper();
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static AresAuthorityService aresAuthorityService;

    public static AresAuthorityService getAresAuthorityService() {
        if(null == aresAuthorityService) {
            aresAuthorityService = SpringContextUtils.getBean(AresAuthorityService.class);
        }
        return aresAuthorityService;
    }

    public static Session buildSessionForRequest(HttpServletRequest request) throws IllegalArgumentException {
        Assert.notNull(request, "request不能为空");

        String accessToken;
        String freshToken;
        String authCode;
        Session session = null;
        AresAuthority authority = null;
        Date now = new Date();
        String url = urlPathHelper.getPathWithinApplication(request);
        if(StringUtils.isNotBlank(accessToken = request.getHeader(HTTP_HEADER_ACCESS_TOKEN))) {
            authority = getAresAuthorityService().queryByAccessToken(accessToken);

            // 访问令牌超时配置，单位秒
            int timeout = ConfigUtils.getValue(ConfigName.SESSION_THIRD_ACCESS_TOKEN_TIMEOUT,
                    ConfigName.SESSION_THIRD_ACCESS_TOKEN_TIMEOUT_DEFVAL());

            if(null == authority || null == authority.getLastAccessTime() ||
                    now.getTime() - authority.getLastAccessTime().getTime() > timeout * 1000) {
                throw new IllegalArgumentException("访问令牌已过期");
            }

            authority.setLastAccessTime(now);
        } else if(StringUtils.isNotBlank(freshToken = request.getHeader(HTTP_HEADER_FRESH_TOKEN))) {
            authority = getAresAuthorityService().queryByFreshToken(freshToken);

            // 刷新令牌超时配置，单位秒
            int timeout = ConfigUtils.getValue(ConfigName.SESSION_THIRD_REFRESH_TOKEN_TIMEOUT,
                    ConfigName.SESSION_THIRD_REFRESH_TOKEN_TIMEOUT_DEFVAL());

            if(null == authority || null == authority.getFreshLastAccessTime() ||
                    now.getTime() - authority.getFreshLastAccessTime().getTime() > timeout * 1000) {
                throw new IllegalArgumentException("刷新令牌已过期");
            }

            if(!pathMatcher.match(ThirdAppCommonController.FRESH_ACCESS_TOKEN_URL, url)) {
                throw new IllegalArgumentException("请请求正确刷新令牌接口地址");
            }
            authority.setFreshLastAccessTime(now);

        } else if(StringUtils.isNotBlank(authCode = request.getHeader(HTTP_HEADER_AUTH_CODE))) {
            authority = getAresAuthorityService().queryByAuthCode(authCode);
            // 授权码超时配置，单位秒
            int timeout = ConfigUtils.getValue(ConfigName.SESSION_THIRD_AUTH_CODE_TIMEOUT,
                    ConfigName.SESSION_THIRD_AUTH_CODE_TIMEOUT_DEFVAL);

            if(null == authority || null == authority.getCodeCreateTime() ||
                    now.getTime() - authority.getCodeCreateTime().getTime() > timeout * 1000) {
                throw new IllegalArgumentException("授权码超时，请重新申请");
            }

            if("1".equals(authority.getCodeStatus())) {
                throw new IllegalArgumentException("授权码已被使用，请重新申请");
            }

            if(!pathMatcher.match(ThirdAppCommonController.GAIN_ACCESS_TOKEN_URL, url)) {
                throw new IllegalArgumentException("请请求正确获取访问令牌接口地址");
            }
        } else {
            return null;
        }

        // 验证门户会话是否超时
        ValidateSession portalSession = (ValidateSession) SessionManagerUtils.getDefaultManager().getSessionDao()
                .get(authority.getPortalSessionId());
        boolean isOk = null != portalSession;
        if(null != portalSession) {
            try {
                portalSession.validate();
            } catch (SessionException e) {
                isOk = false;
            }
        }
        if(!isOk) {
            throw new IllegalArgumentException("门户会话已经超时");
        }

        session = new SimpleSession((String) null);

        session.setAttribute(CAS_SESSION_ARES_AUTHORITY, authority);
        session.setSkey(authority.getSecretKey());
        ThreadContext.bindSession(session);

        getAresAuthorityService().save(authority);

        return session;
    }

    /**
     * 得到当前第三方会话
     * @param session 会话，为空时自动获取当前会话
     * @return
     */
    public static AresAuthority getAresAuthority(Session session) {
        if(null == session) {
            session = SecurityUtils.getSession();
        }
        return null == session ? null : (AresAuthority) session.getAttribute(CAS_SESSION_ARES_AUTHORITY);
    }

    /**
     * 判断是否为第三方会话
     * @param session 会话，为空时自动获取当前会话
     * @return
     */
    public static boolean isThirdSession(Session session) {
        return null != getAresAuthority(null);
    }

}
