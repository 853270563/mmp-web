package cn.com.yitong.framework.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UrlPathHelper;

import cn.com.yitong.core.session.util.SessionManagerUtils;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.core.util.ThreadContext;

/**
 * 自动注册线程上下文的拦截器
 * @author lc3@yitong.com.cn
 */
public class ThreadContextFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(ThreadContextFilter.class);

    public static final String LOGIN_URLS_KEY = "loginUrls";
    public static final String UNCODEC_URL_KEY = "unCodecUrls";

    private final List<String> loginUrlList = new ArrayList<String>();
    private final List<String> unCodecUrlList = new ArrayList<String>();

    public final UrlPathHelper urlPathHelper = new UrlPathHelper();
    public final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        initConfig(filterConfig, LOGIN_URLS_KEY, loginUrlList);
        initConfig(filterConfig, UNCODEC_URL_KEY, unCodecUrlList);
    }

    protected void initConfig(FilterConfig filterConfig, String paramKey, List<String> params) {
        String paramsStr = filterConfig.getInitParameter(paramKey);
        if(null != paramsStr && !paramsStr.isEmpty()) {
            paramsStr = paramsStr.trim();
            if(logger.isDebugEnabled()) {
                logger.debug(paramKey + ":" + paramsStr);
            }
            String[] split = paramsStr.split(",|\n|;");
            for (String s : split) {
                s = s.trim();
                if(s.isEmpty() || params.contains(s)) {
                    continue;
                }
                params.add(s);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            ThreadContext.remove();
            if (request instanceof HttpServletRequest) {
                // 绑定当前会话到当前线程上下文环境
                ReusableHttpServletRequest req = new ReusableHttpServletRequest((HttpServletRequest) request);

                // 判断是否为登录操作
                String url = urlPathHelper.getPathWithinApplication(req);
                for (String loginUrl : loginUrlList) {
                    if (antPathMatcher.match(loginUrl, url)) {
                        ThreadContext.setIsLogining(true);
                        if (logger.isDebugEnabled()) {
                            logger.debug("接收到登录请求:" + url);
                        }
                        break;
                    }
                }

                for (String unCodecUrl : unCodecUrlList) {
                    if (antPathMatcher.match(unCodecUrl, url)) {
                        ThreadContext.setCodecType(0);
                        break;
                    }
                }

                ThreadContext.bindHttpRequest(req);

                chain.doFilter(req, response);
            } else {
                chain.doFilter(request, response);
            }
        } catch (Exception e) {
            logger.error("拦截器执行异常", e);
        } finally {
            SessionManagerUtils.getDefaultManager().submit(SecurityUtils.getSession());
            ThreadContext.remove();
        }
    }

    @Override
    public void destroy() {
        ThreadContext.remove();
    }
}
